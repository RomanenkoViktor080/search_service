package school.faang.search_service.exception.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import school.faang.search_service.exception.LoggableException;

@Getter
public abstract class ApiException extends LoggableException {
    private final HttpStatus status;

    protected ApiException(String message, String debugMessage) {
        super(message);
        this.debugMessage = debugMessage;
        this.status = getDefaultStatus();
    }

    protected abstract HttpStatus getDefaultStatus();
}
