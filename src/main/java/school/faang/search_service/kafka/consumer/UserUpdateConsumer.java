package school.faang.search_service.kafka.consumer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.json.JsonData;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.avro.user.UserAddSkills;
import school.faang.avro.user.UserUpdate;
import school.faang.search_service.document.user.User;
import school.faang.search_service.mapper.UserMapper;
import school.faang.search_service.service.UserService;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@KafkaListener(topics = "${spring.kafka.topics.user-update.name}")
@Component
public class UserUpdateConsumer {
    private static final String SCRIPT_TYPE = "painless";
    private static final String ADD_SKILLS_SCRIPT = """
                    if (ctx._source.filters == null) {
                      ctx._source.filters = [];
                    }
                    if (ctx._source.skills == null) {
                      ctx._source.skills = [];
                    }
                    for (newSkill in params.skills) {
                      boolean replaced = false;
                      for (int i = 0; i < ctx._source.filters.size(); i++) {
                        if (ctx._source.filters.get(i).code == 'skill'
                            && ctx._source.filters.get(i).value == newSkill.id.toString()) {
                          replaced = true;
                          break;
                        }
                      }
                      if (!replaced) {
                        ctx._source.skills.add(newSkill);
                        ctx._source.filters.add([
                            'title':'Скилл',
                            'code':'skill',
                            'value':newSkill.id.toString(),
                            'value_title':newSkill.title.toString()
                        ]);
                      }
                    }
                """;

    private final UserService userService;
    private final UserMapper userMapper;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchClient client;
    @Value("${spring.data.elasticsearch.indexes.users.title}")
    private String index;

    @KafkaHandler
    void updateUserListener(UserUpdate event) {
        log.info("update user event, data: {}", event);
        User user = elasticsearchOperations.get(String.valueOf(event.getId()), User.class);
        if (user == null) {
            throw new EntityNotFoundException("Not found user document");
        }
        userMapper.update(event, user);
        user.setFilters(userService.getAllFilters(user));
        elasticsearchOperations.save(user);
    }

    @KafkaHandler
    void addUserSkillsListener(UserAddSkills event) throws IOException {
        log.info("user add skills, data: {}", event);
        List<Map<String, ? extends Serializable>> skills = event.getSkills().stream()
                .map(skill -> Map.of(
                        "id", skill.getId(),
                        "title", skill.getTitle()
                ))
                .collect(Collectors.toList());

        Map<String, JsonData> params = Map.of(
                "skills", JsonData.of(skills)
        );

        UpdateRequest<User, Void> request = UpdateRequest.of(u -> u
                .index(index)
                .id(String.valueOf(event.getId()))
                .script(Script.of(s -> s
                        .lang(SCRIPT_TYPE)
                        .source(ADD_SKILLS_SCRIPT)
                        .params(params)
                ))
                .docAsUpsert(false)
        );

        UpdateResponse<User> response = client.update(request, User.class);

        if (response.result() != Result.Updated) {
            log.error("Cannot update user document. Response {}, data: {}", response, event);
            throw new RuntimeException("Cannot update user document");
        }
    }
}
