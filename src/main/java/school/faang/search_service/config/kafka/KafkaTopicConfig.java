package school.faang.search_service.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import school.faang.search_service.kafka.KafkaTopic;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic userCreated() {
        return TopicBuilder.name(KafkaTopic.USER_CREATE.getName()).build();
    }

    @Bean
    public NewTopic promotionUserEvents() {
        return TopicBuilder.name("promotion.user.events").build();
    }

    @Bean
    public NewTopic searchUserImpressionEvents() {
        return TopicBuilder.name("search.user.impression").build();
    }

    @Bean
    public NewTopic promotionTariffEvents() {
        return TopicBuilder.name("promotion.tariff.events").build();
    }
}
