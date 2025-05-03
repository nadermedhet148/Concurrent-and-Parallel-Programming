package io.eventLoop;

import io.eventLoop.events.HttpEvent;
import io.eventLoop.events.TimeDiffEvent;
import io.eventLoop.events.DoneEvent;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class Main {
    static Integer num = 0;
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        EventLoop eventLoop = new EventLoop();

        eventLoop.start();
//    eventLoop.addEventHandler(TimeDiffEvent.class, (event) -> {
//      event.getCurrentTimeDiff();
//      eventLoop.dispatch(new DoneEvent());
//    });
//
//    eventLoop.addEventHandler(DoneEvent.class, DoneEvent::done);
//
//    eventLoop.dispatch(new TimeDiffEvent(System.currentTimeMillis()));

        eventLoop.addEventHandler(HttpEvent.class, (event) -> {
            try {
                getHttpEventConsumer(event);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });


        eventLoop.addEventHandler(HttpEvent.class, (event) -> {
            System.out.println("Method: " + event.getMethod());
            System.out.println("URL: " + event.getUrl());
            System.out.println("Body: " + event.getBody());
        });

        var res = eventLoop.dispatch(new HttpEvent("https://jsonplaceholder.typicode.com/posts", "POST", ""));

        System.out.println(res.get());

    }

    private static void getHttpEventConsumer(HttpEvent event) {
        try {
            num++;
            event.complete(event.sendRequest());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
