package joh.faust.playground.verticles;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;

public class WatcherVerticle extends AbstractVerticle {

    @Override
    public Uni<Void> asyncStart() {
        vertx.eventBus().consumer("NAME_WATCHER", message -> {
            System.out.println("Found user: " + message.body());
        });

        return super.asyncStart();
    }
}
