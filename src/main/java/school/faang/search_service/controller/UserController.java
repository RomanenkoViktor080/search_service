package school.faang.search_service.controller;

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
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "10") int size,
            @ParameterObject SortCursorDto lastSort,
            @RequestParam(required = false, defaultValue = "0") boolean isIncludeFacets,
            @RequestParam MultiValueMap<String, String> filters
    ) {
        filters.remove("q");
        filters.remove("size");
        filters.remove("lastId");
        filters.remove("lastScore");
        filters.remove("isIncludeFacets");
        System.out.println(lastSort);
        System.out.println(filters);
        return searchService.searchUsers(q, size, lastSort, isIncludeFacets, filters);
    }
}
