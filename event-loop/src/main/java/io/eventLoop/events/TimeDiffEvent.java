package io.eventLoop.events;

import lombok.Getter;

@Getter
public class TimeDiffEvent extends AbstractEvent<Long> {

    private final long timestamp;


    public TimeDiffEvent(long timestamp) {
        super();
        this.timestamp = timestamp;
    }


    public void getCurrentTimeDiff() {
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - timestamp;
        System.out.println("Time difference: " + timeDiff + " milliseconds");
    }

}
