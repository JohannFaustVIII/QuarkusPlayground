package joh.faust.playground;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

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
    }
}
