package io.eventLoop;

import io.eventLoop.events.HttpEvent;
import io.eventLoop.events.TimeDiffEvent;
import io.eventLoop.events.DoneEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main {

  public static void main(String[] args) throws ExecutionException, InterruptedException {

    EventLoop eventLoop = new EventLoop();

    eventLoop.start();
    eventLoop.addEventHandler(TimeDiffEvent.class, (event) -> {
      event.getCurrentTimeDiff();
      eventLoop.dispatch(new DoneEvent());
    });

    eventLoop.addEventHandler(DoneEvent.class, DoneEvent::done);

    eventLoop.dispatch(new TimeDiffEvent(System.currentTimeMillis()));

    eventLoop.addEventHandler(HttpEvent.class, (event) -> {
      try {
        event.complete(event.sendRequest());
      } catch (IOException | InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

    var res = eventLoop.dispatch(new HttpEvent("https://jsonplaceholder.typicode.com/posts", "POST", ""));

    System.out.println(res.get());

  }

}
