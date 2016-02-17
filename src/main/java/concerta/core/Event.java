package concerta.core;

import java.util.Objects;

public class Event {
    private final EventType type;
    private final int t;

    public Event(EventType type, int t) {
        this.type = type;
        this.t = t;
    }

    public EventType getType() {
        return type;
    }

    public int getTime() {
        return t;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return type == event.type && t == event.t;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, t);
    }

    @Override
    public String toString() {
        return type + "@" + t;
    }
}
