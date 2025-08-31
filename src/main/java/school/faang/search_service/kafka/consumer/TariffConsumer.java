package school.faang.search_service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.avro.tariff.CreateTariffEvent;
import school.faang.avro.tariff.UpdateTariffEvent;
import school.faang.search_service.service.TariffService;

@Slf4j
@RequiredArgsConstructor
@KafkaListener(topics = "${spring.kafka.topics.promotion-tariffs-events.name}")
@Component
public class TariffConsumer {
    private final TariffService tariffService;

    @KafkaHandler
    void createTariffListener(CreateTariffEvent event) {
        tariffService.create(event);
    }

    @KafkaHandler
    void updateTariffListener(UpdateTariffEvent event) {
        tariffService.update(event);
    }
}
