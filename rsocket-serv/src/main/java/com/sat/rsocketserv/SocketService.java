package com.sat.rsocketserv;

import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Publisher;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

public class SocketService implements RSocket {

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        System.out.println("SocketService fireAndForget Called with payload "+ payload.getDataUtf8());
        return Mono.empty();
    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        System.out.println("SocketService requestResponse Called with payload "+ payload.getDataUtf8());
        //return RSocket.super.requestResponse(payload);
        return  Mono.fromCallable(() -> {
            return DefaultPayload.create("Hello from SocketService, my ServerPayload");
        });
    }

    @Override
    public Flux<Payload> requestStream(Payload payload) {
        System.out.println("SocketService requestStream Called with payload "+ payload.getDataUtf8());


        //This generates 3 elements

        /*
        return Flux.fromIterable(List.of("Hello World", "Hello Australia", "Hello RSocket"))
                .map(t -> t.toUpperCase())
                .doOnNext(System.out::println)
                .delayElements(Duration.ofSeconds(2))
                .doFinally(s-> System.out.println("Stream over last element - " + s))
                .map(t -> DefaultPayload.create(t));
        */


        // This generates unlimited random numbers.


        Random random = new Random();
        random.nextLong();
        return Flux.generate((payloadSynchronousSink) -> {
            DefaultPayload payload1 = (DefaultPayload) DefaultPayload.create(String.valueOf(random.nextLong()));
            payloadSynchronousSink.next(payload1);

        });


        /*

        Flux<Payload> flux = Flux.generate(
                () -> "t",
                (state, sink) -> {
                    DefaultPayload payload1 = (DefaultPayload) DefaultPayload.create(state);
                    //sink.next("3 x "+state+" = " + state.concat(state));
                    sink.next(payload1);
                    state = state.concat(state);
                    if (state == "ttt")
                        sink.complete();
                    return  state;
                }
        );
        return flux;
        */


    }


    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        System.out.println("SocketService fireAndForget Called with payload "+ payloads.toString());
        return Flux.from(payloads)
                .map(item -> Integer.parseInt(item.getDataUtf8())*10)
                .map(integer -> DefaultPayload.create(integer.toString()))
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(System.out::println);
    }
}
