package school.faang.search_service.exception.kafka;

import school.faang.search_service.exception.LoggableException;

public class EventNotFoundException extends LoggableException {
    public EventNotFoundException(String string) {
        super(string);
    }
}
