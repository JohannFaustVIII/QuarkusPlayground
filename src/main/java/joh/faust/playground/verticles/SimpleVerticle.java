package joh.faust.playground.verticles;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.Json;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;

public class SimpleVerticle extends AbstractVerticle {

    private long counter = 0;

    @Override
    public Uni<Void> asyncStart() {
        vertx.setPeriodic(2000L, tick -> counter++);

        Router router = Router.router(vertx);
        router.get("/hello/:name").handler(this::getHello);

        return vertx.createHttpServer()
                .requestHandler(router)
                .listen(8000)
                .onItem()
                .invoke(() -> System.out.println("Check localhost:8000"))
                .onFailure()
                .invoke(Throwable::printStackTrace)
                .replaceWithVoid();
    }

    private void getHello(RoutingContext routingContext) {
        String name = routingContext.request().getParam("name");

        String response = "Hello World to " + name + "; Counter = " + counter;

        routingContext.response()
                .putHeader("content-type", "application/json")
                .setStatusCode(200)
                .endAndForget(Json.encodePrettily(response));
    }



    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        System.out.println("Starting...");
        vertx.deployVerticle(SimpleVerticle::new, new DeploymentOptions())
                        .subscribe()
                                .with(System.out::println);
        System.out.println("Complete.");
    }
}
