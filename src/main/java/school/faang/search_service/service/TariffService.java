package school.faang.search_service.service;

import school.faang.avro.tariff.CreateTariffEvent;
import school.faang.avro.tariff.UpdateTariffEvent;


public interface TariffService {
    void create(CreateTariffEvent dto);

    void update(UpdateTariffEvent dto);
}
