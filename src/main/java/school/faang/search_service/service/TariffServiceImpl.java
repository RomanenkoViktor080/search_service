package school.faang.search_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.search_service.entity.Tariff;
import school.faang.search_service.kafka.dto.tariff.CreateTariffEvent;
import school.faang.search_service.kafka.dto.tariff.UpdateTariffEvent;
import school.faang.search_service.mapper.TariffMapper;
import school.faang.search_service.repository.sql.TariffRepository;

@RequiredArgsConstructor
@Service
public class TariffServiceImpl implements TariffService {
    private final TariffMapper tariffMapper;
    private final TariffRepository tariffRepository;

    @Override
    public void create(CreateTariffEvent dto) {
        Tariff tariff = tariffMapper.toTariff(dto);
        tariffRepository.save(tariff);
    }

    @Override
    public void update(UpdateTariffEvent dto) {
        Tariff tariff = tariffRepository.getByIdOrThrow(dto.id());
        tariffMapper.update(dto, tariff);
        tariffRepository.save(tariff);
    }
}
