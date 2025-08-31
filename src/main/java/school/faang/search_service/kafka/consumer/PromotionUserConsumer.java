package school.faang.search_service.kafka.consumer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.avro.user.UserChangeTariffEvent;
import school.faang.search_service.document.user.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@KafkaListener(topics = "${spring.kafka.topics.promotion-users-events.name}")
@Component
public class PromotionUserConsumer {
    private static final String SCRIPT_TYPE = "painless";
    private static final String SCRIPT = """
                ctx._source.tariff_id = params.user.tariffId;
                ctx._source.promotion_id = params.user.promotionId;
            """;

    @Value("${spring.data.elasticsearch.indexes.users.title}")
    private String index;

    private final ElasticsearchClient client;

    @KafkaHandler
    public void listener(UserChangeTariffEvent event) {

        Map<String, Object> data = new HashMap<>();
        data.put("tariffId", event.getTariffId());
        data.put("promotionId", event.getPromotionId());

        UpdateRequest<User, Void> request = UpdateRequest.of(u -> u
                .index(index)
                .id(String.valueOf(event.getUserId()))
                .script(Script.of(s -> s
                        .lang(SCRIPT_TYPE)
                        .source(SCRIPT)
                        .params("user", JsonData.of(data))
                ))
                .docAsUpsert(false)
        );

        try {
            UpdateResponse<User> response = client.update(request, User.class);

            if (response.result() != Result.Updated) {
                log.error("Cannot update user document. Response {}, data: {}", response, event);
                throw new RuntimeException("Cannot update user document");
            }
        } catch (IOException e) {
            log.error("Cannot update user document. Data: {}", event, e);
            throw new RuntimeException(e);
        }
    }
}
