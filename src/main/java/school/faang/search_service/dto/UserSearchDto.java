package school.faang.search_service.dto;

import org.springframework.util.MultiValueMap;

public record UserSearchDto(
        String q,
        MultiValueMap<String, String> filters
) {
}