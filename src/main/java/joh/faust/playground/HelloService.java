package joh.faust.playground;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@ApplicationScoped
public class HelloService {

    @ConfigProperty(name = "greeting")
    private String greeting;

    public String politeHello(String name) {
        return greeting + " " + name;
    }

}
