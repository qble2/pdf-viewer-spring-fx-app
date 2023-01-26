package qble2.pdf.viewer.gui.event;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeadEventHanlder {

  @Subscribe
  public void handleDeadEvent(DeadEvent deadEvent) {
    log.warn("dead event: {}", deadEvent.getEvent());
  }

}
