package school.faang.search_service.kafka.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record EnvelopeMessage(
        String type,
        JsonNode payload
) {
}
