package concerta.notification;

import concerta.core.Event;
import concerta.core.EventType;

import java.util.HashMap;
import java.util.Map;

import static concerta.core.EventType.*;

public class EventMessageFormatter {
    private Map<EventType, String> eventMessages = new HashMap<>();

    public EventMessageFormatter() {
        fillMessages();
    }

    private void fillMessages() {
        eventMessages.put(STARTING, "Starting time slice - %d min");
        eventMessages.put(IN_PROGRESS, "%d min left");
        eventMessages.put(WILL_ELAPSE_SOON, "%d min to go");
        eventMessages.put(ELAPSED, "Time slice elapsed - %d min");
    }

    public String message(Event event) {
        return eventMessages.get(event.getType());
    }
}
