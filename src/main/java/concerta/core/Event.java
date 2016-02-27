package concerta.core;

import java.time.Duration;
import java.util.Objects;

public final class Event {
    private final EventType type;
    private final Duration duration;

    private Event(EventType type, Duration duration) {
        this.type = type;
        this.duration = duration;
    }

    @SuppressWarnings("StaticMethodNamingConvention")
    public static Event of(EventType type, Duration duration) {
        return new Event(type, duration);
    }

    public EventType getType() {
        return type;
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Event event = (Event) obj;
        return type == event.type &&
            Objects.equals(duration, event.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, duration);
    }

    @Override
    public String toString() {
        return type + "-" + duration;
    }
}
