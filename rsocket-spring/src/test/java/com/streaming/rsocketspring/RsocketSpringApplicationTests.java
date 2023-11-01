package com.streaming.rsocketspring;

import com.streaming.rsocketspring.dto.ChartResponseDto;
import com.streaming.rsocketspring.dto.PayloadRequestDto;
import com.streaming.rsocketspring.dto.PayloadResponseDto;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

//@SpringBootTest(properties = "spring.rsocket.server.port=6565")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RsocketSpringApplicationTests {
	private RSocketRequester requester;
	@Autowired
	private RSocketRequester.Builder builder;
	@BeforeAll
	public void setup(){
		this.requester = this.builder
				.transport(TcpClientTransport.create("localhost", 6565));
	}
	@Test
	public void destinationVariableCall() {
		this.requester.route("math.service.print.55")
				.data(new PayloadRequestDto(0))
				.send();
	}
	@Test
	public void fireAndForget(){
		Mono<Void> mono = this.requester.route("math.service.print")
				.data(new PayloadRequestDto(5))
				.send();
		StepVerifier.create(mono)
				.verifyComplete();
	}
	@Test
	public void requestResponse(){
		Mono<PayloadResponseDto> mono = this.requester.route("math.service.square")
				.data(new PayloadRequestDto(5))
				.retrieveMono(PayloadResponseDto.class)
				.doOnNext(System.out::println);
		StepVerifier.create(mono)
				.expectNextCount(1)
				.verifyComplete();
	}
	@Test
	public void requestStream() {

		Flux<PayloadResponseDto> responseDtoFlux = this.requester.route("math.service.table")
				.data(new PayloadRequestDto(3))
				.retrieveFlux(PayloadResponseDto.class)
				//.log()
				.doOnNext(System.out::println);
		StepVerifier.create(responseDtoFlux)
				.expectNextCount(10)
				.verifyComplete();
	}
	@Test
	public void requestChannel() {
		Flux<PayloadRequestDto> requestDtoFlux = Flux.range(-10, 21)
				.map(PayloadRequestDto::new);
		Flux<ChartResponseDto> responseDtoFlux = this.requester.route("math.service.chart")
				.data(requestDtoFlux)
				.retrieveFlux(ChartResponseDto.class)
				.doOnNext(System.out::println);
		StepVerifier.create(responseDtoFlux)
				.expectNextCount(21)
				.verifyComplete();
	}

}
