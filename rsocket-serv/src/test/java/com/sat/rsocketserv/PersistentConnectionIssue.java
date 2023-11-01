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
import reactor.test.StepVerifier;

import java.time.Duration;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PersistentConnectionIssue {
    private RSocket rSocket;
    @BeforeAll
    void setup() {
        this.rSocket = RSocketConnector.create()
                .setupPayload(DefaultPayload.create("user:password"))
                .connect(TcpClientTransport.create("127.0.0.1", 6565))
                .block();
    }
    @Test
    public void requestStream() throws InterruptedException {
        //TODO: Run this against FastProducerService, implement SocketAcceptorImpl to use RSocket for FastProducerService
        //Run this and during wait restart the RSocketServApplication and see the error later
        Payload payload = DefaultPayload.create("Hello World");
        Flux<String> stringFlux = this.rSocket.requestStream(payload)
                .map(payload1 -> payload1.getDataUtf8())
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(System.out::println)
                .take(2)
                .doFinally(System.out::println);

        StepVerifier.create(stringFlux)
                .expectNextCount(2)
                .verifyComplete();

        try {
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //TODO: This results in error as it throws the Closed channel exception because server restarted
        //to fix this we need to use RSocketClient instead of RSocket directly.
        Flux<String> stringFlux2 = this.rSocket.requestStream(payload)
                .map(payload1 -> payload1.getDataUtf8())
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(System.out::println)
                .take(10)
                .doFinally(System.out::println);

        StepVerifier.create(stringFlux2)
                .expectNextCount(10)
                .verifyComplete();



    }
}
