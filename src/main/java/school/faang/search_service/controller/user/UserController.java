package school.faang.search_service.controller.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.search_service.dto.SortCursorDto;
import school.faang.search_service.dto.UserSearchResponseDto;
import school.faang.search_service.service.UserSearchService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserSearchService searchService;

    @GetMapping("/search")
    public UserSearchResponseDto searchUsers(
            @RequestParam(required = false)
            @Schema(description = "Текстовый поисковый запрос")
            String q,

            @RequestParam(required = false, defaultValue = "10")
            @Schema(description = "Максимальное количество пользователей в ответе", example = "10")
            int size,

            @ParameterObject
            @Schema(description = "Информация о сортировки последнего пользователя. Используется для пагинации")
            SortCursorDto lastSort,

            @RequestParam(required = false, defaultValue = "0")
            @Schema(description = "Включать ли фасеты (агрегации)", example = "true")
            boolean includeFacets,

            @RequestParam
            @Schema(description = "Фильтры для поиска (например, по скиллам, странам и т.д.)")
            MultiValueMap<String, String> filters
    ) {
        removeQueryParamsFromFilters(filters);
        return searchService.searchUsers(q, size, lastSort, includeFacets, filters);
    }

    private void removeQueryParamsFromFilters(MultiValueMap<String, String> filters) {
        filters.remove("q");
        filters.remove("size");
        filters.remove("lastId");
        filters.remove("lastScore");
        filters.remove("includeFacets");
    }
}
