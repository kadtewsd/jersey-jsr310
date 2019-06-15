package my.jersey;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MyJacksonConfigurator implements ContextResolver<ObjectMapper> {

    private final ObjectMapper objectMapper;
    public MyJacksonConfigurator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return this.objectMapper;
    }
}

