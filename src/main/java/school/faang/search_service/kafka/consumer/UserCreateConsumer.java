package school.faang.search_service.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.avro.user.UserCreate;
import school.faang.search_service.document.user.User;
import school.faang.search_service.mapper.UserMapper;
import school.faang.search_service.service.UserService;

@RequiredArgsConstructor
@KafkaListener(topics = "${spring.kafka.topics.user-create.name}")
@Component
public class UserCreateConsumer {
    private final UserService userService;
    private final UserMapper mapper;
    private final ElasticsearchOperations elasticsearchOperations;

    @KafkaHandler
    void listener(UserCreate data) throws JsonProcessingException {
        User user = mapper.toUser(data);
        user.setFilters(userService.getAllFilters(user));
        elasticsearchOperations.save(user);
    }

}
