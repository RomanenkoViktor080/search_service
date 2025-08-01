package school.faang.search_service.kafka.dto.user;

import school.faang.search_service.document.country.CountryFilter;

public record UserCreate(
        long id,
        boolean active,
        CountryFilter country

) {
}
