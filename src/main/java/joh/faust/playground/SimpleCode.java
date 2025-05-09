package joh.faust.playground;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.time.Duration;

public class SimpleCode {

    public static void main(String[] args) {
        Uni.createFrom().item("Faust")
                .onItem().transform(s -> "Hello " + s)
                .onItem().transform(String::toUpperCase)
                .onItem().transformToMulti(s -> Multi.createFrom().generator(() -> -1, (n, emitter) -> {
                    int index = n + 1;
                    if (index < s.length()) {
                        emitter.emit(s.charAt(index));
                    } else {
                        emitter.complete();
                    }
                    return index;
                }))
                .onItem().transform(c -> "Character: " + c)
                .subscribe().with(System.out::println);

        invokeAndCall();
        asyncTransform();
    }

    private static void invokeAndCall() {
        Uni.createFrom().item("Call exceution").onItem().call(() -> Uni.createFrom().item("?").onItem().delayIt().by(Duration.ofSeconds(1)))
                .subscribe().with(System.out::println);

        Uni.createFrom().item("Invoke execution").onItem().invoke(s -> System.out.println(s + " call"))
                        .subscribe().with(System.out::println);

        Uni.createFrom().item("Another call").onSubscription().invoke(() -> System.out.println("Subscribed"))
                .subscribe().with(System.out::println);
    }

    private static void asyncTransform() {
        Uni.createFrom().item("Async Transformed Faust")
                .onItem().transformToUni(s -> Uni.createFrom().item("Hello " + s))
                .subscribe().with(System.out::println);

        Multi.createFrom().items(6, 1, 7)
                .onItem().transformToUniAndMerge(i -> Uni.createFrom().item("Hello Merged " + i).onItem().delayIt().by(Duration.ofSeconds(i)))
                .subscribe().with(System.out::println);

        Multi.createFrom().items(6, 1, 7)
                .onItem().transformToUniAndConcatenate(i -> Uni.createFrom().item("Hello Concatenated " + i).onItem().delayIt().by(Duration.ofSeconds(i)))
                .subscribe().with(System.out::println);
    }
}
