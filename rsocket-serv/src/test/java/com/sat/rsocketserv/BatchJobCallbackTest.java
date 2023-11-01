package com.sat.rsocketserv;

import com.sat.rsocketserv.service.CallBackService;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BatchJobCallbackTest {
    private RSocket rSocket;
    @BeforeAll
    void setup() {
        this.rSocket = RSocketConnector.create()
                .acceptor(SocketAcceptor.with(new CallBackService()))
                .setupPayload(DefaultPayload.create("user:password"))
                .connect(TcpClientTransport.create("127.0.0.1", 6565))
                .block();
    }
    @Test
    public void fireAndForget() throws InterruptedException {
        Payload payload = DefaultPayload.create("Hello World from BatchJobCallBackTest");
        Mono<Void> mono = this.rSocket.fireAndForget(payload);

        StepVerifier.create(mono)
                .verifyComplete();

        try {
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
