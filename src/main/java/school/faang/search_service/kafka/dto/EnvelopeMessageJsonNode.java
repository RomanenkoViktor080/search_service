package school.faang.search_service.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface EnvelopeMessageJsonNode {
    @JsonIgnore
    String getType();

    long getId();
}
