package joh.faust.playground;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class HelloResource {

    @Inject
    HelloService helloService;


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello RESTEasy";
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/polite/{name}")
    public String greeting(@PathParam("name") String name) {
        return helloService.politeHello(name);
    }
}