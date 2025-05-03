package io.eventLoop.threading;

import io.eventLoop.EventLoop;
import io.eventLoop.events.AbstractEvent;
import lombok.Getter;
import lombok.extern.java.Log;

@Log
@Getter
public class DispatcherThread implements Runnable {

    private final EventLoop eventLoop;



    public DispatcherThread(EventLoop eventLoop) {
        this.eventLoop = eventLoop;
    }


    @Override
    public void run() {
        log.fine(String.format("[EventLoop] Started new DispatcherThread %s", Thread.currentThread()));

        try {
            while (!Thread.currentThread().isInterrupted() && !getEventLoop().getDispatchExecutor().isShutdown()) {
                AbstractEvent<?> abstractEvent = getEventLoop().getAbstractEventQueue().take();
                getEventLoop().getTaskExecutor().submit(new ExecutorThread<>(getEventLoop(), abstractEvent));
            }
        } catch (InterruptedException e) {
            log.fine("[EventLoop] Interrupted DispatcherThead " + Thread.currentThread());
        }
    }

}
