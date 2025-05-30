package joh.faust.playground.verticles;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.http.RequestOptions;
import io.vertx.mutiny.core.eventbus.Message;

import java.time.Duration;

public class WatcherVerticle extends AbstractVerticle {

    private long counter = 0;

    private boolean started = false;

    @Override
    public Uni<Void> asyncStart() {
        vertx.eventBus().consumer("NAME_WATCHER", message -> {
            String user = (String) message.body();
            if (user.equals("Watcher")) {
                if (!started) {
                    started = true;
                    reply(vertx.eventBus().request("PING_PONG", "pong " + counter));
                }
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

    private void reply(Uni<Message<Object>> uni) {
        uni.onItem().delayIt().by(Duration.ofSeconds(1)).
                subscribe().with(mess -> {
                    counter++;
                    String body = (String) mess.body();
                    System.out.println("Watcher Received: " + body);
                    reply(mess.replyAndRequest("pong " + counter));
                });
    }
}
