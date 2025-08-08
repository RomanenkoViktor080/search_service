package school.faang.search_service.service;

import school.faang.search_service.kafka.dto.tariff.CreateTariffEvent;
import school.faang.search_service.kafka.dto.tariff.UpdateTariffEvent;

public interface TariffService {
    void create(CreateTariffEvent dto);

    void update(UpdateTariffEvent dto);
}
