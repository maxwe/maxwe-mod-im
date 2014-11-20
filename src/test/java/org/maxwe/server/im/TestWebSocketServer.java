package org.maxwe.server.im;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.WebSocket;
import org.vertx.java.platform.Verticle;

/**
 * Created by dingpengwei on 11/14/14.
 */
public class TestWebSocketServer extends Verticle {

    @Override
    public void start(){
        HttpClient client = vertx.createHttpClient().setHost("localhost").setPort(8080);
        client.connectWebsocket("/", new Handler<WebSocket>() {
            public void handle(WebSocket ws) {
                System.out.println("the connect is build");
            }
        });
    }


}
