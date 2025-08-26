package school.faang.search_service.repository.sql;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.repository.CrudRepository;
import school.faang.search_service.entity.Tariff;

public interface TariffRepository extends CrudRepository<Tariff, Long> {
    default Tariff getByIdOrThrow(long tariffId) {
        return findById(tariffId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Tariff %d not found", tariffId)));
    }
}
