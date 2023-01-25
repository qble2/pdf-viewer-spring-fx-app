package qble2.document.viewer.gui.event;

import java.util.EventListener;
import com.google.common.eventbus.EventBus;

public class EventBusFx implements EventListener {

  private EventBus eventBus;

  private static final EventBusFx INSTACE = new EventBusFx();

  private EventBusFx() {
    this.eventBus = new EventBus();
  }

  public static EventBusFx getInstance() {
    return INSTACE;
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
