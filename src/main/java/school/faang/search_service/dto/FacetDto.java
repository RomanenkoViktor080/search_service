package school.faang.search_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;


public record FacetDto(
        @Schema(description = "Отображаемое название значения фасета (label), которое показывается пользователю",
                example = "Страна"
        )
        String title,

        @Schema(
                description = "Технический код фасета, используемое для фильтрации и передачи в запросе",
                example = "country"
        )
        String code,

        @Schema(description = "Набор фасетов. Возвращается, если в запросе был передан нужные параметры")
        List<FacetValueDto> values
) {
}
