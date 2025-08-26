package school.faang.search_service.exception.api;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends ApiException {
    public EntityNotFoundException(String message) {
        super(message, message);
    }

    public EntityNotFoundException(String message, String debugMessage) {
        super(message, debugMessage);
    }

    @Override
    protected HttpStatus getDefaultStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
