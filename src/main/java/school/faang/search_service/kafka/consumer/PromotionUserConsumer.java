package school.faang.search_service.kafka.consumer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.search_service.document.user.User;
import school.faang.search_service.kafka.dto.EnvelopeMessage;
import school.faang.search_service.kafka.dto.user.UserChangeTariffEvent;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class PromotionUserConsumer {
    @Value("${spring.data.elasticsearch.indexes.users}")
    private String index;

    private final ObjectMapper objectMapper;
    private final ElasticsearchClient client;

    @KafkaListener(topics = "promotion.user.events")
    public void listener(String data) throws JsonProcessingException {
        EnvelopeMessage envelopeMessage = objectMapper.readValue(data, EnvelopeMessage.class);
        switch (envelopeMessage.type()) {
          case ("USER_CHANGE_TARIFF"): {
              userChangeTariffEvent(objectMapper.treeToValue(
                      envelopeMessage.payload(),
                      UserChangeTariffEvent.class
              ));
              break;
          }
          default: {
              throw new RuntimeException("Event not found");
          }
        }
    }

    private void userChangeTariffEvent(UserChangeTariffEvent event) {
        String scriptSource = """
                    ctx._source.tariff_id = params.user.tariffId;
                    ctx._source.promotion_id = params.user.promotionId;
                """;
        UpdateRequest<User, Void> request = UpdateRequest.of(u -> u
                .index(index)
                .id(String.valueOf(event.userId()))
                .script(Script.of(s -> s
                        .lang("painless")
                        .source(scriptSource)
                        .params("user", JsonData.of(event))
                ))
                .docAsUpsert(false)
        );

        try {
            UpdateResponse<User> response = client.update(request, User.class);

            if (!response.result().toString().equals("Updated")) {
                log.error("Cannot update user document. Response {}, data: {}", response, event);
                throw new RuntimeException("Cannot update user document");
            }
        } catch (IOException e) {
            log.error("Cannot update user document. Data: {}", event, e);
            throw new RuntimeException(e);
        }
    }
}
