package school.faang.search_service.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.FilterAggregate;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import school.faang.search_service.document.user.User;
import school.faang.search_service.dto.FacetDto;
import school.faang.search_service.dto.FacetValueDto;
import school.faang.search_service.dto.SortCursorDto;
import school.faang.search_service.dto.UserDto;
import school.faang.search_service.dto.UserSearchResponseDto;
import school.faang.search_service.kafka.dto.user.UserViewEvent;
import school.faang.search_service.kafka.producer.UserViewProducer;
import school.faang.search_service.mapper.UserMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserSearchServiceImpl implements UserSearchService {
    private static final List<String> SEARCH_FIELDS = List.of("headline", "about_me");
    private static final String ALL_FACETS_AGGS = "all_facets";
    private static final String FACETS_FILTERS_AGGS = "facets_filters";
    private static final String FACETS_NAMES_AGGS = "names";
    private static final String FACETS_NAME_TITLE_AGGS = "name_title";
    private static final String FACETS_VALUE_TITLE_AGGS = "value_title";
    private static final String FILTERS_PATH = "filters";
    private static final String FILTERS_CODE_PATH = "filters.code";
    private static final String FILTERS_VALUE_PATH = "filters.value";

    private final UserMapper userMapper;
    private final ElasticsearchClient client;
    private final UserViewProducer userViewProducer;
    @Value("${spring.data.elasticsearch.indexes.users}")
    private String index;

    @Override
    public UserSearchResponseDto searchUsers(
            String q,
            int size,
            SortCursorDto sortCursorDto,
            boolean isIncludeFacets,
            MultiValueMap<String, String> filters
    ) {
        SearchRequest request = SearchRequest.of(
                search -> {
                    search.index(index)
                            .size(size)
                            .sort(so -> so.field(f -> f.field("id").order(SortOrder.Desc)))
                            .sort(so -> so.score(score -> score.order(SortOrder.Desc)))
                            .trackScores(true)
                            .query(buildMainQuery(q));
                    if (sortCursorDto != null && sortCursorDto.lastId() != null && sortCursorDto.lastScore() != null) {
                        search.searchAfter(List.of(
                                FieldValue.of(sortCursorDto.lastId()),
                                FieldValue.of(sortCursorDto.lastScore())
                        ));
                    }
                    if (isIncludeFacets) {
                        search.aggregations(buildAggregations(filters));
                    }
                    return search.postFilter(buildPostFilterQuery(filters));
                }
        );
        try {
            return buildResponse(client.search(request, User.class), isIncludeFacets);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Query buildMainQuery(
            String query
    ) {
        BoolQuery.Builder bool = new BoolQuery.Builder();

        bool.filter(f -> f.term(t -> t.field("is_active").value(true)));

        if (query != null && !query.isBlank()) {
            bool.must(must -> must.multiMatch(multi -> multi
                    .query(query)
                    .operator(Operator.And)
                    .fields(SEARCH_FIELDS)
            ));
        }

        return Query.of(q -> q.bool(bool.build()));
    }

    private Query buildPostFilterQuery(
            MultiValueMap<String, String> filters
    ) {
        return buildFiltersQuery("all", filters, false);
    }

    private Map<String, Aggregation> buildAggregations(
            MultiValueMap<String, String> filters
    ) {
        Map<String, Aggregation> aggregations = new HashMap<>();

        aggregations.put(ALL_FACETS_AGGS, buildAggregation(ALL_FACETS_AGGS, filters, false));
        if (!filters.isEmpty()) {
            for (String facet : filters.keySet()) {
                aggregations.put(facet, buildAggregation(facet, filters, true));
            }
        }

        return aggregations;
    }

    private Aggregation buildAggregation(
            String slug, Map<String, List<String>> filters,
            boolean isConcreteFacet
    ) {
        return Aggregation.of(aggs -> aggs
                .aggregations(
                        FACETS_FILTERS_AGGS,
                        facetFilters -> facetFilters
                                .nested(nest -> nest
                                        .path(FILTERS_PATH)
                                )
                                .aggregations(FACETS_NAMES_AGGS, aggsNames -> {
                                    if (isConcreteFacet) {
                                        aggsNames.filter(filter -> filter.term(term -> term
                                                        .field(FILTERS_CODE_PATH)
                                                        .value(slug)
                                                )
                                        );
                                    } else {
                                        aggsNames.terms(
                                                terms -> terms
                                                        .field(FILTERS_CODE_PATH)
                                                        .size(10)
                                        );
                                    }
                                    return aggsNames.aggregations(
                                                    "values",
                                                    vv -> vv.terms(terms -> terms
                                                                    .field(FILTERS_VALUE_PATH)
                                                            )
                                                            .aggregations(
                                                                    FACETS_VALUE_TITLE_AGGS,
                                                                    buildAggregationTopHits("filters.value_title")
                                                            )
                                            )
                                            .aggregations(
                                                    FACETS_NAME_TITLE_AGGS,
                                                    buildAggregationTopHits("filters.title")
                                            );
                                })
                )
                .filter(buildFiltersQuery(slug, filters, isConcreteFacet))
        );
    }

    private Aggregation buildAggregationTopHits(String field) {
        Aggregation.Builder builder = new Aggregation.Builder();
        builder.topHits(
                topHits -> topHits
                        .source(
                                source -> source
                                        .filter(filter -> filter
                                                .includes(field)
                                        )
                        )
                        .size(1)
        );
        return builder.build();
    }

    private Query buildFiltersQuery(
            String slug, Map<String, List<String>> filters,
            boolean isConcreteFacet
    ) {
        Query.Builder filterQuery = new Query.Builder();
        BoolQuery.Builder boolFilter = new BoolQuery.Builder();

        filters.entrySet()
                .stream()
                .filter(entry -> !isConcreteFacet || !entry.getKey().equals(slug))
                .forEach(filter -> boolFilter.filter(
                        buildFilterQuery(
                                filter.getKey(),
                                filter.getValue()
                        )));

        filterQuery.bool(boolFilter.build());
        return filterQuery.build();
    }

    private Query buildFilterQuery(
            String filterKey,
            List<String> values
    ) {
        BoolQuery.Builder queryBool = new BoolQuery.Builder();
        queryBool
                .filter(ff -> ff.term(term -> term
                                .field(FILTERS_CODE_PATH)
                                .value(filterKey)
                        )
                )
                .filter(ff -> ff.terms(terms -> terms
                        .field(FILTERS_VALUE_PATH)
                        .terms(tt -> tt.value(values.stream()
                                .map(FieldValue::of)
                                .toList()))
                ));

        Query.Builder query = new Query.Builder();
        query.nested(nested -> nested
                .path(FILTERS_PATH)
                .query(q ->
                        q.bool(queryBool.build())
                )
        );
        return query.build();
    }

    private UserSearchResponseDto buildResponse(
            SearchResponse<User> searchResponse,
            boolean isIncludeFacets
    ) {
        HitsMetadata<User> hits = searchResponse.hits();
        long total = hits.total() != null ? hits.total().value() : 0;
        List<UserDto> users = hits.hits().stream()
                .map(userHit -> userMapper.toUserDto(userHit.source()))
                .toList();

        sendUsersViewsEvents(hits.hits());

        SortCursorDto lastSort = null;
        if (!hits.hits().isEmpty()) {
            List<FieldValue> sort = hits.hits().get(hits.hits().size() - 1).sort();
            lastSort = new SortCursorDto(sort.get(0)._toJsonString(), sort.get(1).doubleValue());
        }
        UserSearchResponseDto.UserSearchResponseDtoBuilder response = UserSearchResponseDto.builder()
                .users(users)
                .total(total)
                .lastSort(lastSort);
        if (isIncludeFacets) {
            addFacetsToResponse(response, searchResponse);
        }
        return response.build();
    }

    private void addFacetsToResponse(
            UserSearchResponseDto.UserSearchResponseDtoBuilder response,
            SearchResponse<User> searchResponse
    ) {
        Map<String, Aggregate> aggregations = searchResponse.aggregations();
        if (aggregations.containsKey(ALL_FACETS_AGGS)) {
            FilterAggregate allFacets = aggregations.get(ALL_FACETS_AGGS).filter();
            List<FacetDto> facets = allFacets.aggregations()
                    .get(FACETS_FILTERS_AGGS).nested().aggregations()
                    .get(FACETS_NAMES_AGGS).sterms().buckets().array()
                    .stream().map(name -> {
                        String code = name.key().stringValue();
                        Map<String, Aggregate> mainAggregations = name.aggregations();
                        if (aggregations.containsKey(code)) {
                            mainAggregations = aggregations.get(code).filter()
                                    .aggregations()
                                    .get(FACETS_FILTERS_AGGS)
                                    .nested()
                                    .aggregations()
                                    .get(FACETS_NAMES_AGGS)
                                    .filter()
                                    .aggregations();
                        }
                        String title = extractFieldTopHits("title", mainAggregations.get(FACETS_NAME_TITLE_AGGS));
                        List<FacetValueDto> values = mainAggregations.get("values").sterms().buckets().array()
                                .stream().map(valueBucket -> {
                                    String valueTitle = extractFieldTopHits(
                                            "value_title",
                                            valueBucket.aggregations().get(FACETS_VALUE_TITLE_AGGS)
                                    );
                                    long count = valueBucket.docCount();
                                    String value = valueBucket.key().stringValue();
                                    return new FacetValueDto(valueTitle, value, count);
                                }).toList();
                        return new FacetDto(title, code, values);
                    }).toList();
            response.facets(facets);
        }
    }

    private String extractFieldTopHits(
            String field,
            Aggregate aggs
    ) {
        List<Hit<JsonData>> titleHits = aggs.topHits().hits().hits();
        if (!titleHits.isEmpty()) {
            Hit<JsonData> titleHit = titleHits.get(0);
            if (titleHit.source() != null) {
                Map<String, String> map = titleHit.source().to(Map.class);
                if (map.containsKey(field)) {
                    return map.get(field);
                }
            }
        }
        log.warn("failed extract field: {}, aggregation: {}", field, aggs);
        return "Error";
    }

    private void sendUsersViewsEvents(List<Hit<User>> hits) {
        hits.forEach(userHit -> {
            User source = userHit.source();
            if (source != null && source.getPromotionId() != null) {
                UserViewEvent event = UserViewEvent.builder()
                        .userId(source.getId())
                        .promotionId(source.getPromotionId())
                        .build();
                userViewProducer.onView(event);
            }
        });
    }
}
