package school.faang.search_service.exception.api;

import org.springframework.http.HttpStatus;

public class DataValidationException extends ApiException {
    public DataValidationException(String message) {
        super(message, message);
    }

    public DataValidationException(String message, String debugMessage) {
        super(message, debugMessage);
    }

    @Override
    protected HttpStatus getDefaultStatus() {
        return HttpStatus.UNPROCESSABLE_ENTITY;
    }
}
