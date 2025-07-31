package school.faang.search_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record FacetValueDto(
        @Schema(description = "Отображаемое название значения фасета (label), которое показывается пользователю",
                example = "Россия"
        )
        String title,

        @Schema(
                description = "Техническое значение значения фасета, используемое для фильтрации и передачи в запросе",
                example = "1"
        )
        String value,

        @Schema(
                description = "Количество документов (например, пользователей), соответствующих этому значению",
                example = "10"
        )
        long count
) {
}