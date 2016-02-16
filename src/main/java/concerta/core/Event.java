package concerta.core;

import java.util.Objects;

public class Event {
    private final EventType type;

    public Event(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return type == event.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
