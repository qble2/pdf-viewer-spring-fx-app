package qble2.pdf.viewer.gui.event;

import java.util.EventListener;
import org.springframework.stereotype.Component;
import com.google.common.eventbus.EventBus;

@Component
public class EventBusFx {

  private EventBus eventBus;

  public EventBusFx() {
    this.eventBus = new EventBus();
  }

  public void registerListener(EventListener listener) {
    this.eventBus.register(listener);
  }

  public void unregisterListener(EventListener listener) {
    this.eventBus.unregister(listener);
  }

  public void notify(Object event) {
    this.eventBus.post(event);
  }

}
