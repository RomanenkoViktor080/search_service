package school.faang.search_service.dto;

public record FacetValueDto(
        String name,
        String value,
        long count
) {
}