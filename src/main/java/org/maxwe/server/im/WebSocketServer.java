package org.maxwe.server.im;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.ServerWebSocket;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

/**
 * Created by dingpengwei on 11/14/14.
 */
public class WebSocketServer extends Verticle {

    private static final String CHANNEL = "im.maxwe.org";

    @Override
    public void start() {
        final Logger logger = container.logger();
        final HttpServer server = vertx.createHttpServer();
        server.websocketHandler(new Handler<ServerWebSocket>() {
                                    public void handle(final ServerWebSocket ws) {
                                        logger.info("=============receive a connect==============");

                                        final EventBus eventBus = vertx.eventBus();
                                        final Handler handler = new Handler<Message>() {
                                            @Override
                                            public void handle(Message message) {
                                                String messageString = message.body().toString();
                                                logger.info("get published message : " + messageString);
                                                ws.writeTextFrame(messageString);
                                            }
                                        };

                                        eventBus.registerHandler(CHANNEL, handler);

                                        ws.dataHandler(new Handler<Buffer>() {
                                                           @Override
                                                           public void handle(Buffer s) {
                                                               logger.info("------------ receive data will publish ----------");
                                                               eventBus.publish(CHANNEL, s.toString());
                                                           }
                                                       }
                                        );

                                        ws.exceptionHandler(new Handler<Throwable>() {
                                                                @Override
                                                                public void handle(Throwable event) {
                                                                    logger.info("exception when handler exception : " + event.getMessage());
                                                                    eventBus.unregisterHandler(CHANNEL, handler, new AsyncResultHandler<Void>() {
                                                                        public void handle(AsyncResult<Void> asyncResult) {
                                                                            System.out.println("The handler has been unregistered across the cluster ok? " + asyncResult.succeeded());
                                                                        }
                                                                    });
                                                                }
                                                            }

                                        );
                                        ws.closeHandler(new Handler<Void>() {
                                                            @Override
                                                            public void handle(Void event) {
                                                                logger.error("connection close");
                                                                eventBus.unregisterHandler(CHANNEL, handler, new AsyncResultHandler<Void>() {
                                                                    public void handle(AsyncResult<Void> asyncResult) {
                                                                        System.out.println("The handler has been unregistered across the cluster ok? " + asyncResult.succeeded());
                                                                    }
                                                                });
                                                            }
                                                        }

                                        );
                                        ws.endHandler(new Handler<Void>() {
                                                          @Override
                                                          public void handle(Void event) {
                                                              logger.error("connection end");
                                                          }
                                                      }
                                        );
                                    }
                                }

        ).listen(1121);
    }
}
