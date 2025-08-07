package school.faang.search_service.kafka.dto.user;

import lombok.Builder;

@Builder
public record UserViewEvent(
        long userId,
        long promotionId
) {
}
