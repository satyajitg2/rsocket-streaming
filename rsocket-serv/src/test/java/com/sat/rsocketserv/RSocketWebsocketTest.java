package com.sat.rsocketserv;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.util.DefaultPayload;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RSocketWebsocketTest {

    private RSocket rSocket;

    @BeforeAll
    void setup() {
        this.rSocket = RSocketConnector.create()
                .setupPayload(DefaultPayload.create("user:password"))
                .connect(WebsocketClientTransport.create("127.0.0.1", 6565))
                //.connect(TcpClientTransport.create("127.0.0.1", 6565))
                .block();
    }

    @Test
    public void fireAndForget() {
        Payload payload = DefaultPayload.create("Hello World");
        Mono<Void> mono = this.rSocket.fireAndForget(payload);

        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    public void requestResponse() {
        Payload payload = DefaultPayload.create("Hello World as Request Response");
        Mono<String> response = this.rSocket.requestResponse(payload)
                .map(t -> t.getDataUtf8())
                .doOnNext(System.out::println);

        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void requestStream() {
        Payload payload = DefaultPayload.create("RequestChannel payload Hello");
        Flux<String> payloadFlux = this.rSocket.requestStream(payload)
                .map(s -> s.getDataUtf8())
                //.doOnNext(System.out::println)
                .take(100);

        StepVerifier.create(payloadFlux)
                //.expectNextCount(3)
                .expectNextCount(100)
                .verifyComplete();

    }

    @Test
    public void requestChannel()
    {
        Flux<Payload> payloadFlux = Flux.range(10, 8)
                .map(i -> DefaultPayload.create(Integer.toString(i)))
                .doOnNext(s -> System.out.println("Emitting item "+ s.toString()));

        Flux<String> flux = this.rSocket.requestChannel(payloadFlux)
                .map(payload -> Integer.parseInt(payload.getDataUtf8()))
                .map(integer -> integer.toString())
                .doOnNext(System.out::println);

        StepVerifier.create(flux)
                .expectNextCount(8)
                .verifyComplete();
    }

}
