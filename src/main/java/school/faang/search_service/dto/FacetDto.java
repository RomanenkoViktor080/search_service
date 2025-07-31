package school.faang.search_service.dto;

import java.util.List;


public record FacetDto(
        String name,
        String code,
        List<FacetValueDto> values
) {
}
