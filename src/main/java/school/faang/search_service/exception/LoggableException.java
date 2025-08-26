package school.faang.search_service.exception;

import lombok.Getter;

@Getter
public abstract class LoggableException extends RuntimeException {
    protected String debugMessage;

    protected LoggableException(String message) {
        super(message);
        this.debugMessage = message;
    }
}
