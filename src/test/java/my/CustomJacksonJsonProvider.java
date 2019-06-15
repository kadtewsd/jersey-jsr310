package my;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import javax.annotation.Priority;
import javax.ws.rs.ext.Provider;


@Provider
@Priority(0)
public class CustomJacksonJsonProvider extends JacksonJaxbJsonProvider {
    public CustomJacksonJsonProvider(ObjectMapper objectMapper) {
        super();
        super.setMapper(objectMapper);
    }
}
