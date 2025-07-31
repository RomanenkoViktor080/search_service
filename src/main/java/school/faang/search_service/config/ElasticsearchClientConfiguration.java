package school.faang.search_service.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories()
public class ElasticsearchClientConfiguration extends ElasticsearchConfiguration {
    @Value("${spring.data.elasticsearch.port}")
    private String port;
    @Value("${spring.data.elasticsearch.host}")
    private String host;
    private ElasticsearchClient elasticsearchClient;

    @Bean
    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(String.format("%s:%s", host, port))
                .build();
    }
}
