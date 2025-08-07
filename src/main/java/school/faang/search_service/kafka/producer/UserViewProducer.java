package school.faang.search_service.kafka.producer;

import school.faang.search_service.kafka.dto.user.UserViewEvent;

public interface UserViewProducer {
    void onView(UserViewEvent event);
}
