package com.sat.rsocketserv;

import io.rsocket.RSocket;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.transport.netty.server.WebsocketServerTransport;


public class RsocketServApplication {

	public static void main(String[] args) {
		RSocketServer server = RSocketServer.create(new SocketAcceptorImpl());
		CloseableChannel closeableChannel = server.bindNow(TcpServerTransport.create("localhost", 6565));
		//CloseableChannel closeableChannel = server.bindNow(WebsocketServerTransport.create("localhost", 6565));
		System.out.println("RSocketServer Application started ");
		//keep listening
		closeableChannel.onClose().block();
	}

}
