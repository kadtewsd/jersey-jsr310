package my;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class ConfigureTest {

    @Bean
    public ObjectMapper customObjectMapper(Environment environment) {
        JavaTimeModule m = new JavaTimeModule();
        m.addDeserializer(LocalDateTime.class, new CustomLocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return Jackson2ObjectMapperBuilder.json()
                .modulesToInstall(m)
                // ここを無効化しないと Unix Time でフォーマットされてしまう。
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToEnable(SerializationFeature.INDENT_OUTPUT)
                .build();

    }
}
