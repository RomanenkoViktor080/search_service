package school.faang.search_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.search_service.kafka.dto.user.UserViewEvent;

@RequiredArgsConstructor
@Component
public class UserViewProducerImpl implements UserViewProducer {
    private final KafkaTemplate<String, Object> producer;

    @Override
    public void onView(UserViewEvent event) {
        producer.send("search.user.impression", String.valueOf(event.promotionId()), event);
    }
}
