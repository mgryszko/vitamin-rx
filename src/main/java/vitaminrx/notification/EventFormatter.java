package vitaminrx.notification;

import vitaminrx.core.EventType;

import java.time.Duration;

public interface EventFormatter {
    String format(EventType type, Duration duration);
}
