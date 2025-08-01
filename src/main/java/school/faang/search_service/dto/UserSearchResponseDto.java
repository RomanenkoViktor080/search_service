package school.faang.search_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record UserSearchResponseDto(
        @Schema(description = "Список пользователей")
        List<UserDto> users,

        @Schema(description = "Сколько всего пользователей")
        long total,

        @Schema(description = "Список фильтров")
        List<FacetDto> facets,

        @Schema(description = "Сортировка последнего пользователя на странице. Используется для пагинации")
        SortCursorDto lastSort
) {

}
