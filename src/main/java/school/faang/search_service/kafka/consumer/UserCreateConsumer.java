package school.faang.search_service.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.search_service.document.user.User;
import school.faang.search_service.kafka.dto.user.UserCreate;
import school.faang.search_service.mapper.UserMapper;
import school.faang.search_service.service.UserService;

@RequiredArgsConstructor
@Component
public class UserCreateConsumer {
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final UserMapper mapper;
    private final ElasticsearchOperations elasticsearchOperations;

    @KafkaListener(topics = "user.create")
    void listener(String data) throws JsonProcessingException {
        UserCreate userCreate = objectMapper.readValue(data, UserCreate.class);
        User user = mapper.toUser(userCreate);
        user.setFilters(userService.getAllFilters(user));
        elasticsearchOperations.save(user);
    }

}
