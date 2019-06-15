package my.jersey;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.controller.MyJerseyResource;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.FormContentFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.RequestContextFilter;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

@Component
public class MyJerseyTest {

    final JerseyTest jerseyTest;

    boolean start = false;

    public void setUp() throws Exception{
        if (!start) {
            this.jerseyTest.setUp();
        }
        start = true;
    }

    public WebTarget webTarget(String url) {
        return this.jerseyTest.target(url);
    }

    public MyJerseyTest(ApplicationContext applicationContext, ObjectMapper objectMapper) {
        this.jerseyTest = new JerseyTest() {

            @Override
            public Client getClient() {
                return JerseyClientBuilder.createClient()
                        .register(new MyJacksonConfigurator(objectMapper));
            }

            @Override
            protected ResourceConfig configure() {
                return new ResourceConfig(MyJerseyResource.class)
                        .register(new MyJacksonConfigurator(objectMapper))
                        .property("contextConfig", applicationContext);
            }

            @Override
            protected ServletDeploymentContext configureDeployment() {
                return ServletDeploymentContext
                        .forServlet(new ServletContainer(configure()))
                        .addFilter(HiddenHttpMethodFilter.class, HiddenHttpMethodFilter.class.getSimpleName())
                        .addFilter(FormContentFilter.class, FormContentFilter.class.getSimpleName())
                        .addFilter(RequestContextFilter.class, RequestContextFilter.class.getSimpleName())
                        .build();
            }

            @Override
            public TestContainerFactory getTestContainerFactory() {
                return new GrizzlyWebTestContainerFactory();
            }
        };

    }
}
