package com.sat.rsocketserv;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FastProducerBackPressure {
    private RSocket rSocket;
    @BeforeAll
    void setup() {
        this.rSocket = RSocketConnector.create()
                .setupPayload(DefaultPayload.create("user:password"))
                .connect(TcpClientTransport.create("127.0.0.1", 6565))
                .block();
    }
    @Test
    public void requestStream() {
        //TODO: Run this against FastProducerService, implement SocketAcceptorImpl to use RSocket for FastProducerService
        Payload payload = DefaultPayload.create("Hello World");
        Flux<String> stringFlux = this.rSocket.requestStream(payload)
                .map(payload1 -> payload1.getDataUtf8())
                //delayElements(Duration.ofMillis(25))
                .doOnNext(System.out::println)
                .doFinally(System.out::println);

        StepVerifier.create(stringFlux)
                .expectNextCount(1000)
                .verifyComplete();
    }
}
