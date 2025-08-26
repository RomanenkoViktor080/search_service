package school.faang.search_service.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Value("${spring.kafka.topics.user-create.name}")
    private String userCreateTopic;
    @Value("${spring.kafka.topics.user-update.name}")
    private String userUpdateTopic;
    @Value("${spring.kafka.topics.promotion-users-events.name}")
    private String promotionUsersEventsTopic;
    @Value("${spring.kafka.topics.search-users-impression.name}")
    private String searchUsersImpression;
    @Value("${spring.kafka.topics.promotion-tariffs-events.name}")
    private String promotionTariffsEvents;

    @Bean
    public NewTopic userCreate() {
        return TopicBuilder.name(userCreateTopic).build();
    }

    @Bean
    public NewTopic userUpdate() {
        return TopicBuilder.name(userUpdateTopic).build();
    }

    @Bean
    public NewTopic promotionUsersEvents() {
        return TopicBuilder.name(promotionUsersEventsTopic).build();
    }

    @Bean
    public NewTopic searchUsersImpressionEvents() {
        return TopicBuilder.name(searchUsersImpression).build();
    }

    @Bean
    public NewTopic promotionTariffsEvents() {
        return TopicBuilder.name(promotionTariffsEvents).build();
    }
}
