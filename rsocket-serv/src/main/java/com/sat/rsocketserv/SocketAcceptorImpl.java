package com.sat.rsocketserv;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import reactor.core.publisher.Mono;

public class SocketAcceptorImpl implements SocketAcceptor {
    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload connectionSetupPayload, RSocket rSocket) {
        System.out.println("Socket Acceptor Impl - accept connection");
        //TODO: Implement SocketService for simple Rsocket operations.
        //return Mono.just(new SocketService());


        //TODO: Implement FastProducerService to show Backpressure
        return Mono.fromCallable(FastProducerService::new);
    }
}
