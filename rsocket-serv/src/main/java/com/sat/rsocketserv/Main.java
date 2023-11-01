package com.sat.rsocketserv;

import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.Random;
import java.util.function.Consumer;

public class Main {

    public static class SampleSubscriber<T> extends BaseSubscriber<T> {

        public void hookOnSubscribe(Subscription subscription) {
            System.out.println("Subscribed");
            request(1);
        }

        public void hookOnNext(T value) {
            System.out.println("hookOnNext "+value);
            request(1);
        }
    }
    public static void main(String[] args) {
        System.out.println("Hello World!");

        Flux<Integer> ints = Flux.range(1, 4);
        //ints.subscribe(i -> System.out.println(i));


        SampleSubscriber<Integer> ss = new SampleSubscriber<Integer>();
        //ints.subscribe(ss);

        Random random = new Random();
        random.nextInt(100);
        Flux<Payload> generate = Flux.generate((payloadSynchronousSink) -> {
            DefaultPayload payload1 = (DefaultPayload) DefaultPayload.create(String.valueOf(random.nextInt(100)));
            payloadSynchronousSink.next(payload1);

        });

        generate.subscribe(
            x -> System.out.println(x.getDataUtf8())
        );

    }
}
