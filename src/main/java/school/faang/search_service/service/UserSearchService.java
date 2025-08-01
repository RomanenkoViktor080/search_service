package school.faang.search_service.service;

import org.springframework.util.MultiValueMap;
import school.faang.search_service.dto.SortCursorDto;
import school.faang.search_service.dto.UserSearchResponseDto;

public interface UserSearchService {
    UserSearchResponseDto searchUsers(
            String q,
            int size,
            SortCursorDto sortCursorDto,
            boolean isIncludeFacets,
            MultiValueMap<String, String> filters
    );
}
