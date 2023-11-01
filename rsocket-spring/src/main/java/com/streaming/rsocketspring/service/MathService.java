package com.streaming.rsocketspring.service;

import com.streaming.rsocketspring.dto.ChartResponseDto;
import com.streaming.rsocketspring.dto.PayloadRequestDto;
import com.streaming.rsocketspring.dto.PayloadResponseDto;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MathService{
    //fireandforget
    public Mono<Void> print(Mono<PayloadRequestDto> payloadRequestDtoMono){
        System.out.println("MathService print");
        return payloadRequestDtoMono
                .doOnNext(System.out::println)
                .then();
    }

    //requestresponse
    public Mono<PayloadResponseDto> findSquare(Mono<PayloadRequestDto> requestDtoMono) {
        return requestDtoMono
                .doOnNext(System.out::println)
                .map(PayloadRequestDto::getInput)
                .map(i -> new PayloadResponseDto(i, i*i));
    }

    //requestStream
    public Flux<PayloadResponseDto> tableStream(PayloadRequestDto requestDto) {
        System.out.println("Table Stream payload "+requestDto.getInput());
        return Flux.range(1, 10)
                .map(integer -> new PayloadResponseDto(requestDto.getInput(), requestDto.getInput()*integer));

    }

    //requestChannel - y = x^2+1
    public Flux<ChartResponseDto> charStream(Flux<PayloadRequestDto> payloadRequestDtoFlux){
        return payloadRequestDtoFlux
                .map(PayloadRequestDto::getInput)
                .map(integer -> new ChartResponseDto(integer, (integer*integer)+1));
    }
}
