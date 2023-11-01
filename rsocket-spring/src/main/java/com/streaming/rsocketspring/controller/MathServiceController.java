package com.streaming.rsocketspring.controller;

import com.streaming.rsocketspring.dto.ChartResponseDto;
import com.streaming.rsocketspring.dto.PayloadRequestDto;
import com.streaming.rsocketspring.dto.PayloadResponseDto;
import com.streaming.rsocketspring.service.MathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class MathServiceController {

    @Autowired MathService mathService;

    @MessageMapping("math.service.print.{input}")
    public Mono<Void> print(@DestinationVariable int input){
        System.out.println("Received : " + input);
        return Mono.empty();
    }
    @MessageMapping("math.service.print")
    public Mono<Void> print(Mono<PayloadRequestDto> requestDtoMono) {
        System.out.println("Received mono" +requestDtoMono.toString());
        return this.mathService.print(requestDtoMono);
    }

    @MessageMapping("math.service.square")
    public Mono<PayloadResponseDto> findSquare(Mono<PayloadRequestDto> requestDtoMono) {
        return this.mathService.findSquare(requestDtoMono);
    }

    @MessageMapping("math.service.table")
    public Flux<PayloadResponseDto> tableStream(Mono<PayloadRequestDto> requestDtoMono){
        return requestDtoMono.flatMapMany(this.mathService::tableStream);
        //return this.mathService.tableStream(requestDtoMono.block());
    }
    @MessageMapping("math.service.chart")
    public Flux<ChartResponseDto> chartStream(Flux<PayloadRequestDto> dtoFlux){
        return this.mathService.charStream(dtoFlux);
    }

}
