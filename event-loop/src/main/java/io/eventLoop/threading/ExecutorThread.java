package io.eventLoop.threading;

import io.eventLoop.EventLoop;
import io.eventLoop.events.AbstractEvent;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.function.Consumer;


@Log
@Getter
public class ExecutorThread <D> implements Runnable {

    private final EventLoop eventLoop;

    private final AbstractEvent<D> abstractEvent;



    public ExecutorThread(EventLoop eventLoop, AbstractEvent<D> abstractEvent) {
        this.eventLoop = eventLoop;
        this.abstractEvent = abstractEvent;
    }



    @Override
    public void run() {
        log.fine(String.format("[EventLoop] Started new ExecutorThread for %s", getAbstractEvent().toString()));

        byte priority = abstractEvent.getPriority();
        Thread.currentThread().setPriority((priority < 1 || priority > 10) ? 5 : abstractEvent.getPriority());      // Use default priority if event priority is invalid!
        Thread.currentThread().setName(
                String.format(
                        "ExecutorThread for %s | %s",
                        getAbstractEvent().toString(),
                        Thread.currentThread().getName()
                )
        );

        // RET: No handlers for abstractEvent!
        if (!getEventLoop().getHandlers().containsKey(abstractEvent.getClass()))
            return;

        for (Consumer<? extends AbstractEvent<?>> rawHandler : getEventLoop().getHandlers().get(abstractEvent.getClass())) {

            if (getAbstractEvent().isCanceled()) {
                log.fine(String.format("[EventLoop] Stopped ExecutorThread %s due to abstractEvent cancellation!", Thread.currentThread()));
                break;
            }

            Consumer<AbstractEvent<D>> handler = (Consumer<AbstractEvent<D>>) rawHandler;

            try {
                handler.accept(getAbstractEvent());
            }
            catch (Exception e) {
                log.fine(e.toString());
            }

        }
    }

}
