package joh.faust.playground.verticles;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.Json;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.eventbus.Message;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;

import java.time.Duration;

public class SimpleVerticle extends AbstractVerticle {

    private long ping_counter = 0;

    private long counter = 0;

    @Override
    public Uni<Void> asyncStart() {
        vertx.setPeriodic(2000L, tick -> counter++);

        Router router = Router.router(vertx);
        router.get("/hello/:name").handler(this::getHello);

        vertx.eventBus().consumer("PING_PONG").handler(
                message -> {
                    reply(Uni.createFrom().item(message));
                }
        );

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

        vertx.eventBus().send("NAME_WATCHER", name);

        routingContext.response()
                .putHeader("content-type", "application/json")
                .setStatusCode(200)
                .endAndForget(Json.encodePrettily(response));
    }

    private void reply(Uni<Message<Object>> uni) {
        uni.onItem().delayIt().by(Duration.ofSeconds(1)).
                subscribe().with(mess -> {
                    ping_counter++;
                    String body = (String) mess.body();
                    System.out.println("Simple Received: " + body);
                    reply(mess.replyAndRequest("ping " + ping_counter));
                });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        System.out.println("Starting...");
        vertx.deployVerticle(SimpleVerticle::new, new DeploymentOptions())
                        .subscribe()
                                .with(System.out::println);
        vertx.deployVerticle(WatcherVerticle::new, new DeploymentOptions())
                .subscribe()
                .with(System.out::println);
        System.out.println("Complete.");
    }
}
