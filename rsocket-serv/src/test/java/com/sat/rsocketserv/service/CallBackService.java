package com.sat.rsocketserv.service;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import reactor.core.publisher.Mono;

public class CallBackService implements RSocket {
    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        System.out.println("Client side request payload " + payload.getDataUtf8());
        return Mono.empty();
    }
}
