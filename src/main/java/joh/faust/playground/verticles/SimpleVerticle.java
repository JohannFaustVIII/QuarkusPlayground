package joh.faust.playground.verticles;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.mutiny.core.Vertx;

public class SimpleVerticle extends AbstractVerticle {

    private long counter = 0;

    @Override
    public Uni<Void> asyncStart() {
        vertx.setPeriodic(2000L, tick -> counter++);

        return vertx.createHttpServer()
                .requestHandler(req -> req.response().endAndForget("Hello World @" +counter))
                .listen(8000)
                .onItem()
                .invoke(() -> System.out.println("Check localhost:8000"))
                .onFailure()
                .invoke(Throwable::printStackTrace)
                .replaceWithVoid();
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
