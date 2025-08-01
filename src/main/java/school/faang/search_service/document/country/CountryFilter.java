package school.faang.search_service.document.country;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public record CountryFilter(
        @Field(name = "id", type = FieldType.Long)
        long id,
        @Field(name = "title", type = FieldType.Keyword)
        String title
) {
}
