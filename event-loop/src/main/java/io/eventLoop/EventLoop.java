package io.eventLoop;

import io.eventLoop.events.AbstractEvent;
import io.eventLoop.threading.DispatcherThread;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;


@Log
@Getter
@Setter
public class EventLoop {

    private boolean running;

    private final BlockingQueue<AbstractEvent<?>> abstractEventQueue;

    private ExecutorService dispatchExecutor;

    private ExecutorService taskExecutor;

    private final Map<Class<? extends AbstractEvent<?>>, List<Consumer<? extends AbstractEvent<?>>>> handlers;


    public EventLoop(ExecutorService dispatchExecutor, ExecutorService taskExecutor) {
        this.running = false;
        this.abstractEventQueue = new PriorityBlockingQueue<>();
        this.dispatchExecutor = dispatchExecutor;
        this.taskExecutor = taskExecutor;
        this.handlers = new ConcurrentHashMap<>();
    }

    public EventLoop() {
        this(Executors.newSingleThreadExecutor(), Executors.newWorkStealingPool());
    }


    public <E extends AbstractEvent<?>> void addEventHandler(Class<E> clazz, Consumer<E> handler) {

        if (clazz == null || handler == null) return;

        if (!getHandlers().containsKey(clazz))
            getHandlers().put(clazz, new ArrayList<>());

        getHandlers().get(clazz).add(handler);
    }

    public <E extends AbstractEvent<?>> boolean removeEventHandler(Class<E> clazz, Consumer<E> handler) {

        if (handler == null) return false;

        // RET: No handlers for class
        if (!getHandlers().containsKey(clazz))
            return false;

        return getHandlers().get(clazz).remove(handler);

    }


    public <D, E extends AbstractEvent<D>> CompletableFuture<D> dispatch(E event) {

        // RET: Invalid event.
        if (event == null || event.getId() == null) return null;

        log.fine("[EventLoop] Dispatching event " + event);

        CompletableFuture<D> callback = new CompletableFuture<>();
        event.addCallback(callback);
        getAbstractEventQueue().add(event);
        return callback;
    }


    public void start() {

        // RET: Already running!
        if (isRunning()) return;

        getDispatchExecutor().submit(new DispatcherThread(this));
        setRunning(true);

        log.fine("[EventLoop] Started!");
    }

    public void stop() {

        // RET: Not running.
        if (!isRunning()) return;

        getDispatchExecutor().shutdown();
        getTaskExecutor().shutdown();
        setRunning(false);
        log.fine("[EventLoop] Stopped!");
    }

}
