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
                subscription = clock.subscribe(ti -> publish("toCLient", "Remote second: " + ti.getValue()));
            } else if (event.type() == BridgeEventType.SOCKET_CLOSED) {
                log.info("A socket was created");
                subscription.unsubscribe();
            }
            event.complete(true);
        });

        router.route("/eventbus/*").handler(ebHandler);

        // EventBus & handler
        // Allow for static routes (css, images, etc)
        router.route().handler(StaticHandler.create());

        // Start server
        final String webPort = System.getProperty("WEB_PORT");
        final int port = webPort != null ? Integer.valueOf(webPort) : 8080;
        final HttpServer server = vertx.createHttpServer();
        server.requestHandler(router::accept).listen(port);
    }

    private void publish(final String destination, final String data) {
        vertx.eventBus().publish(destination, data);
    }

    public static void main(String[] args) {
        System.setProperty("vertx.disableFileCaching ", "true");
        Vertx.vertx().deployVerticle(ApplicationVerticle.class.getName());
    }
}