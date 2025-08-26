package school.faang.search_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Используется для пагинации. В запросе указывается для получения следующей страницы")
public record SortCursorDto(
        @Schema(description = "Id последнего пользователя на странице")
        String lastId,

        @Schema(description = "Score последнего пользователя на странице")
        Double lastScore
) {
}
