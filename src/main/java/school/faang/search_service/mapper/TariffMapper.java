package school.faang.search_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import school.faang.avro.tariff.CreateTariffEvent;
import school.faang.avro.tariff.UpdateTariffEvent;
import school.faang.search_service.entity.Tariff;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TariffMapper {
    Tariff toTariff(CreateTariffEvent dto);

    void update(UpdateTariffEvent dto, @MappingTarget Tariff entity);
}
