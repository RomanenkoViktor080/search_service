package school.faang.search_service.repository.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import school.faang.search_service.document.user.User;

public interface UserRepository extends ElasticsearchRepository<User, Long> {
}
