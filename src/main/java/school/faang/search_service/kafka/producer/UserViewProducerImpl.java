package school.faang.search_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.avro.user.UserViewEvent;

@RequiredArgsConstructor
@Component
public class UserViewProducerImpl implements UserViewProducer {
    @Value("${spring.kafka.topics.search-users-impression.name}")
    private String searchUsersImpression;

    private final KafkaTemplate<String, UserViewEvent> producer;

    @Override
    public void onView(UserViewEvent event) {
        producer.send(searchUsersImpression, String.valueOf(event.getPromotionId()), event);
    }
}
