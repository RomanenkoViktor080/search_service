package school.faang.search_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import school.faang.search_service.entity.Tariff;
import school.faang.search_service.kafka.dto.tariff.CreateTariffEvent;
import school.faang.search_service.kafka.dto.tariff.UpdateTariffEvent;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TariffMapper {
    Tariff toTariff(CreateTariffEvent dto);

    void update(UpdateTariffEvent dto, @MappingTarget Tariff entity);
}
