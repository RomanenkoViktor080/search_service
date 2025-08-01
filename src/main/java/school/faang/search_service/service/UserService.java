package school.faang.search_service.service;

import org.springframework.stereotype.Service;
import school.faang.search_service.document.user.User;
import school.faang.search_service.document.user.UserFilter;

import java.util.Set;

@Service
public interface UserService {
    Set<UserFilter> getAllFilters(User user);
}
