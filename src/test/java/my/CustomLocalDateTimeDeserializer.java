package my;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomLocalDateTimeDeserializer extends LocalDateTimeDeserializer {

    public CustomLocalDateTimeDeserializer(DateTimeFormatter pattern) {
        super(pattern);
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        if (
                p.hasTokenId(JsonTokenId.ID_STRING) ||
                        p.isExpectedStartArrayToken()) {
            return super.deserialize(p, ctxt);
        } else {
            ObjectNode node = p.getCodec().readTree(p);
            return LocalDateTime.of(
                    node.get("year").asInt(),
                    node.get("monthValue").asInt(),
                    node.get("dayOfMonth").asInt(),
                    node.get("hour").asInt(),
                    node.get("minute").asInt(),
                    node.get("second").asInt()
            );
        }
    }
}

