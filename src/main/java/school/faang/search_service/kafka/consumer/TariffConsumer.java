package school.faang.search_service.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.search_service.kafka.dto.EnvelopeMessage;
import school.faang.search_service.kafka.dto.tariff.CreateTariffEvent;
import school.faang.search_service.kafka.dto.tariff.UpdateTariffEvent;
import school.faang.search_service.service.TariffService;

@Slf4j
@RequiredArgsConstructor
@Component
public class TariffConsumer {
    private final ObjectMapper objectMapper;
    private final TariffService tariffService;

    @KafkaListener(topics = "promotion.tariff.events")
    void listener(String data) throws JsonProcessingException {
        EnvelopeMessage envelope = objectMapper.readValue(data, EnvelopeMessage.class);
        switch (envelope.type()) {
          case "TARIFF_CREATE" -> {
              createTariff(objectMapper.treeToValue(
                      envelope.payload(),
                      CreateTariffEvent.class
              ));
          }
          case "TARIFF_UPDATE" -> {
              updateTariff(objectMapper.treeToValue(
                      envelope.payload(),
                      UpdateTariffEvent.class
              ));
          }
          default -> throw new RuntimeException("Unknown event type: " + envelope.type());
        }
    }

    private void createTariff(CreateTariffEvent event) {
        tariffService.create(event);
    }

    private void updateTariff(UpdateTariffEvent event) {
        tariffService.update(event);
    }
}
