package school.faang.search_service.kafka.dto.user.update;

import school.faang.search_service.document.country.CountryFilter;

public record UserUpdate(
        long id,
        Integer experience,
        boolean active,
        String headline,
        String aboutMe,
        CountryFilter country
) implements UserUpdateEvent {
    @Override
    public String getType() {
        return "USER_UPDATE";
    }

    @Override
    public long getId() {
        return id;
    }
}
