package school.faang.search_service.exception.api;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApiException {
    public ForbiddenException(String message) {
        super(message, message);
    }

    public ForbiddenException(String message, String debugMessage) {
        super(message, debugMessage);
    }


    @Override
    protected HttpStatus getDefaultStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
