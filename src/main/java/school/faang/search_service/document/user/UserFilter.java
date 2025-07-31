package school.faang.search_service.document.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserFilter {
    @Field(name = "value", type = FieldType.Keyword)
    private String value;

    @Field(name = "value_title", type = FieldType.Keyword)
    private String valueTitle;

    @Field(name = "title", type = FieldType.Keyword)
    private String title;

    @Field(name = "code", type = FieldType.Keyword)
    private String code;

}
