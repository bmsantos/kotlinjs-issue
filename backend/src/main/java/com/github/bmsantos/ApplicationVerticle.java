package com.github.bmsantos;

import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.StaticHandler;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.TimeInterval;

import static io.vertx.rxjava.ext.web.Router.router;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class ApplicationVerticle extends AbstractVerticle {

    private Observable<TimeInterval<Long>> clock;
    private Subscription subscription;


    @Override
    public void start() throws Exception {
        log.info("Successfully create ApplicationVerticle");

        clock = Observable.interval(1, SECONDS).timeInterval();

        final Router router = router(vertx);
        router.route().handler(BodyHandler.create());

        // EventBus / WebSockets
        final BridgeOptions options = new BridgeOptions()
          .addOutboundPermitted(new PermittedOptions().setAddress("toClient"));

        final SockJSHandler ebHandler = SockJSHandler.create(vertx).bridge(options, event -> {
            if (event.type() == BridgeEventType.SOCKET_CREATED) {
                log.info("A socket was created");
                if (subscription == null) {
                    subscription = clock.subscribe(ti -> {
                        publish("toClient", "Remote second: " + ti.getValue());
                    });
                }
            } else if (event.type() == BridgeEventType.SOCKET_CLOSED) {
                log.info("A socket was closed");
                subscription.unsubscribe();
                subscription = null;
            }
            event.complete(true);
        });

        router.route("/eventbus/*").handler(ebHandler);

        router.route().handler(StaticHandler.create());

        final HttpServer server = vertx.createHttpServer();
        server.requestHandler(router::accept).listen(8080);
    }

    private void publish(final String destination, final String data) {
        vertx.eventBus().publish(destination, data);
    }

    public static void main(String[] args) {
        System.setProperty("vertx.disableFileCaching ", "true");
        Vertx.vertx().deployVerticle(ApplicationVerticle.class.getName());
    }
}