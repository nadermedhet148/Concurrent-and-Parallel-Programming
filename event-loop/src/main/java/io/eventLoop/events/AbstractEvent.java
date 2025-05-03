package io.eventLoop.events;

import lombok.Getter;
import lombok.Synchronized;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
public class AbstractEvent<D> implements Comparable<AbstractEvent<?>> {

    // ======================   VARS

    /**
     * The priority used to order events in the {@link java.util.concurrent.PriorityBlockingQueue}.
     */
    private final byte priority;

    /**
     * A unique id which is used internally to map & execute matching callbacks.
     */
    private final UUID id;

    /**
     * Used by the handling logic to determine whether succeeding handlers will be skipped.
     */
    private boolean canceled;

    /**
     * All registered callbacks.
     */
    private final Set<CompletableFuture<D>> callbacks;


    public AbstractEvent() {
        this(UUID.randomUUID());
    }

    public AbstractEvent(byte priority) {
        this(UUID.randomUUID(), priority);
    }

    public AbstractEvent(UUID id) {
        this(id, (byte) 5);
    }

    public AbstractEvent(UUID id, byte priority) {
        this.id = id;
        this.priority = priority;
        this.callbacks = new HashSet<>();
    }


    @Synchronized
    public void addCallback(CompletableFuture<D> callback) {
        if (callback == null) return;
        getCallbacks().add(callback);
    }

    public boolean removeCallback(CompletableFuture<D> callback) {
        return getCallbacks().remove(callback);
    }

    public void complete(D data) {
        getCallbacks().forEach(c -> c.complete(data));
    }


    public void complete() {
        getCallbacks().forEach(c -> c.complete(null));
    }


    public void except(Throwable throwable) {
        getCallbacks().forEach(c -> c.completeExceptionally(throwable));
    }

    public void cancel() {
        this.canceled = true;
    }


    @Override
    public int compareTo(AbstractEvent abstractEvent) {
        return Integer.compare(abstractEvent.getPriority(), getPriority());
    }

    @Override
    public String toString() {
        return String.format("[%s-(%d)]", this.getClass().getSimpleName(), getPriority());
    }

}
