package my;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomLocalDateTimeSerializer extends LocalDateTimeSerializer {

    private final String format;

    public CustomLocalDateTimeSerializer(String format) {
        this.format = format;
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            gen.writeString("");
            return;
        }
        gen.writeString(value.format(DateTimeFormatter.ofPattern(format)));
    }
}
