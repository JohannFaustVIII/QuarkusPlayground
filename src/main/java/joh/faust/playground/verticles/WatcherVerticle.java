package joh.faust.playground.verticles;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.http.RequestOptions;

public class WatcherVerticle extends AbstractVerticle {

    @Override
    public Uni<Void> asyncStart() {
        vertx.eventBus().consumer("NAME_WATCHER", message -> {
            String user = (String) message.body();
            if (user.equals("Watcher")) {
                System.out.println("I got my own request.");
            } else {
                System.out.println("Found user: " + message.body());
            }
        });


        vertx.setPeriodic(1000, val -> {
            vertx.createHttpClient().request(
                            new RequestOptions()
                                    .setHost("localhost")
                                    .setPort(8000)
                                    .setURI("/hello/Watcher")
                    ).onItem().transformToUni(req -> req.send())
                    .subscribe().with(res -> {
                        res.body().subscribe().with(buffer -> {
                           String response = new String(buffer.getBytes());
                           System.out.println("Response: " + response);
                        });
                    });

        });

        return super.asyncStart();
    }
}
