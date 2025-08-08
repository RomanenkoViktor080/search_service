package school.faang.search_service.kafka.dto.tariff;

public record UpdateTariffEvent(
        long id,
        double boostFactor,
        boolean active
) {
}
