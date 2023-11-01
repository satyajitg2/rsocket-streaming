package com.sat.rsocketserv;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class BatchJobService implements RSocket {

    private RSocket rSocket;

    BatchJobService(RSocket rSocket) {
        this.rSocket = rSocket;
    }

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        System.out.println("BatchJobService payload "+payload.getDataUtf8());

        Mono.just(payload)
                .delayElement(Duration.ofSeconds(1))
                .doOnNext(System.out::println)
                .flatMap(this::payloadOp)
                .subscribe();

        return Mono.empty();
    }

    private Mono<Void> payloadOp(Payload payload) {
        Payload payload1 = DefaultPayload.create(payload.getDataUtf8()+payload.getDataUtf8());
        return this.rSocket.fireAndForget(payload1);
    }
}
