package school.faang.search_service.document.skill;

import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Builder
public record SkillFilter(
        @Field(name = "id", type = FieldType.Long)
        long id,
        @Field(name = "title", type = FieldType.Keyword)
        String title
) {
}
