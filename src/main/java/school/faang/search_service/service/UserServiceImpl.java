package school.faang.search_service.service;

import org.springframework.stereotype.Service;
import school.faang.search_service.document.user.User;
import school.faang.search_service.document.user.UserFilter;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    public Set<UserFilter> getAllFilters(User user) {
        Set<UserFilter> filters = new HashSet<>();
        filters.add(UserFilter.builder()
                .code("country")
                .title("Страна")
                .value(String.valueOf(user.getCountry().getId()))
                .valueTitle(user.getCountry().getTitle())
                .build());

        if (user.getSkills() != null && !user.getSkills().isEmpty()) {
            filters.addAll(user.getSkills().stream().map(skillFilter -> UserFilter.builder()
                            .code("skill")
                            .title("Скилл")
                            .value(String.valueOf(skillFilter.getId()))
                            .valueTitle(skillFilter.getTitle())
                            .build()
                    ).toList()
            );
        }

        if (user.getExperience() != null) {
            filters.add(UserFilter.builder()
                    .code("experience")
                    .title("Опыт (лет)")
                    .value(String.valueOf(user.getExperience()))
                    .valueTitle(String.valueOf(user.getExperience()))
                    .build());
        }

        return filters;
    }
}
