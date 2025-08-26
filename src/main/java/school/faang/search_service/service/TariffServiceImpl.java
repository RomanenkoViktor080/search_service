package school.faang.search_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.avro.tariff.CreateTariffEvent;
import school.faang.avro.tariff.UpdateTariffEvent;
import school.faang.search_service.entity.Tariff;
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
        Tariff tariff = tariffRepository.getByIdOrThrow(dto.getId());
        tariffMapper.update(dto, tariff);
        tariffRepository.save(tariff);
    }
}
