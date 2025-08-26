package school.faang.search_service.document.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import school.faang.avro.common.CountryFilter;
import school.faang.avro.common.SkillFilter;

import java.util.Set;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "users")
public class User {

    @Id
    @Field(name = "id", type = FieldType.Long)
    private long id;

    @Field(name = "is_active", type = FieldType.Boolean, nullValue = "1")
    @JsonProperty("is_active")
    private boolean active;

    @Field(name = "tariff_id", type = FieldType.Long)
    @JsonProperty("tariff_id")
    private Long tariffId;

    @Field(name = "promotion_id", type = FieldType.Long)
    @JsonProperty("promotion_id")
    private Long promotionId;

    @Field(name = "experience", type = FieldType.Integer)
    private Integer experience;

    @Field(name = "country", type = FieldType.Nested)
    private CountryFilter country;

    @Field(name = "headline", type = FieldType.Text)
    private String headline;

    @Field(name = "about_me", type = FieldType.Text)
    @JsonProperty("about_me")
    private String aboutMe;

    @Field(name = "skills", type = FieldType.Nested)
    private Set<SkillFilter> skills;

    @Field(name = "filters", type = FieldType.Nested)
    private Set<UserFilter> filters;
}
