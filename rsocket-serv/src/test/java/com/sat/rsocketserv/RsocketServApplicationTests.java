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
public class RsocketServApplicationTests {
	private RSocket rSocket;
	@BeforeAll
	void setup() {
		this.rSocket = RSocketConnector.create()
				.setupPayload(DefaultPayload.create("user:password"))
				.connect(TcpClientTransport.create("127.0.0.1", 6565))
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
				//.delayElements(Duration.ofMillis(300))
				.doOnNext(System.out::println)
						.take(1000);

		StepVerifier.create(payloadFlux)
				//.expectNextCount(3)
				.expectNextCount(1000)
				.verifyComplete();

	}

	@Test
	public void requestChannel()
	{
		Flux<Payload> payloadFlux = Flux.range(1, 50)
				.map(i -> DefaultPayload.create(Integer.toString(i)));
				//.doOnNext(s -> System.out.println("Emitting item "+ s.toString()));

		Flux<String> flux = this.rSocket.requestChannel(payloadFlux)
				.map(payload -> Integer.parseInt(payload.getDataUtf8()))
				.map(integer -> integer.toString())
				.doOnNext(s-> System.out.println("server sent "+s));
				//.doOnNext(System.out::println);


		StepVerifier.create(flux)
				.expectNextCount(50)
				.verifyComplete();
	}

}
