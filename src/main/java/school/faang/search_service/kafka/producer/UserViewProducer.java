package school.faang.search_service.kafka.producer;


import school.faang.avro.user.UserViewEvent;

public interface UserViewProducer {
    void onView(UserViewEvent event);
}
