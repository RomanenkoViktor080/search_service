package school.faang.search_service.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record UserSearchResponseDto(
        List<UserDto> users,
        long total,
        List<FacetDto> facets,
        SortCursorDto lastSort
) {

}
