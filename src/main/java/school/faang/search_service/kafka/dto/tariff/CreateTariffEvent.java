package school.faang.search_service.kafka.dto.tariff;

public record CreateTariffEvent(
        double boostFactor,
        boolean active
) {
}
