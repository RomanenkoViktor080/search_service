package school.faang.search_service.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import school.faang.search_service.document.user.User;

public interface UserDocument extends ElasticsearchRepository<User, Long> {
}
