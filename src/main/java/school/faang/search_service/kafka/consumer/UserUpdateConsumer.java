package school.faang.search_service.kafka.consumer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.search_service.document.user.User;
import school.faang.search_service.kafka.dto.EnvelopeMessage;
import school.faang.search_service.kafka.dto.user.update.UserAddSkills;
import school.faang.search_service.kafka.dto.user.update.UserUpdate;
import school.faang.search_service.mapper.UserMapper;
import school.faang.search_service.service.UserService;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserUpdateConsumer {
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchClient client;
    @Value("${spring.data.elasticsearch.indexes.users}")
    private String index;

    @KafkaListener(topics = "user.update")
    void listener(String data) throws IOException {
        EnvelopeMessage envelope = objectMapper.readValue(data, EnvelopeMessage.class);
        switch (envelope.type()) {
          case "USER_ADD_SKILLS" -> {
              UserAddSkills dto = objectMapper.treeToValue(envelope.payload(), UserAddSkills.class);
              addUserSkills(dto);
          }
          case "USER_UPDATE" -> {
              UserUpdate dto = objectMapper.treeToValue(envelope.payload(), UserUpdate.class);
              userUpdate(dto);
          }
          default -> log.warn("Unknown event type: {}", envelope);
        }
    }

    private void userUpdate(UserUpdate dto) {
        User user = elasticsearchOperations.get(String.valueOf(dto.id()), User.class);
        if (user == null) {
            throw new EntityNotFoundException("Not found user document");
        }
        userMapper.update(dto, user);
        user.setFilters(userService.getAllFilters(user));
        elasticsearchOperations.save(user);
    }

    private void addUserSkills(UserAddSkills dto) throws IOException {
        String scriptSource = """
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
        Map<String, JsonData> params = Map.of(
                "skills", JsonData.of(dto.skills())
        );
        UpdateRequest<User, Void> request = UpdateRequest.of(u -> u
                .index(index)
                .id(String.valueOf(dto.id()))
                .script(Script.of(s -> s
                        .lang("painless")
                        .source(scriptSource)
                        .params(params)
                ))
                .docAsUpsert(false)
        );

        UpdateResponse<User> response = client.update(request, User.class);

        if (!response.result().toString().equals("Updated")) {
            log.error("Cannot update user document. Response {}, data: {}", response, dto);
            throw new RuntimeException("Cannot update user document");
        }
    }

}
