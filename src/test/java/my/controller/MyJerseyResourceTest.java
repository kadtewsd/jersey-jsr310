package my.controller;

import my.ConfigureTest;
import my.jersey.MyJerseyTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ConfigureTest.class, MyJerseyTest.class})
public class MyJerseyResourceTest {

    @Autowired
    MyJerseyTest jerseyTest;

    @Before
    public void setUp() throws Exception {
        this.jerseyTest.setUp();
    }

    @Test
    public void postをテストする() {
        Response response = jerseyTest.webTarget("/my").request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(
                        Entity.json(new MyDto(
                                        "start!",
                                        LocalDateTime.of(2018, 1, 1, 12, 1, 1)
                                )
                        )
                );

        assertThat(response.getStatus(), is(200));
        MyDto content = response.readEntity(MyDto.class);
        assertThat(content.word, is("start!2018-01-01T12:01:01 finishes!"));
        assertThat(content.localDateTime.toString(), is(LocalDateTime.of(2019, 1, 1, 12, 0, 0).toString()));
    }

    @Test
    public void getをテストする() {
        Response response = jerseyTest.webTarget("/my").request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();
        assertThat(response.getStatus(), is(200));

        MyDto content = response.readEntity(MyDto.class);
        assertThat(content.word, is("finish!"));
        assertThat(content.localDateTime.toString(), is(LocalDateTime.of(2019, 1, 1, 12, 0, 0).toString()));
    }
}