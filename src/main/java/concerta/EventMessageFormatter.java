package concerta;

import java.util.HashMap;
import java.util.Map;

public class EventMessageFormatter {
    private Map<Event, String> eventMessages = new HashMap<>();

    public EventMessageFormatter() {
        fillMessages();
    }

    private void fillMessages() {
        eventMessages.put(Event.STARTED, "Starting time slice - %d min");
        eventMessages.put(Event.IN_PROGRESS, "%d min left");
        eventMessages.put(Event.WILL_ELAPSE_SOON, "%d min to go");
        eventMessages.put(Event.ELAPSED, "Time slice elapsed - %d min");
    }

    public String message(Event event) {
        return eventMessages.get(event);
    }
}
