package my.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;

@Path("/my")
public class MyJerseyResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MyDto post(MyDto myDto) {
        return new MyDto(
                myDto.value() + " finishes!",
                LocalDateTime.of(2019, 1, 1, 12, 0, 0)
        );
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MyDto get() {
        return new MyDto(
                "finish!",
                LocalDateTime.of(2019, 1, 1, 12, 0, 0)
        );
    }
}
