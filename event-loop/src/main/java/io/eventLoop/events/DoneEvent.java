package io.eventLoop.events;


import lombok.Getter;

@Getter
public class DoneEvent extends AbstractEvent<Long> {


  public DoneEvent() {
    super();
  }


  public void done() {
    System.out.println("event done");
  }

}
