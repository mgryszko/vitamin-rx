package concerta.notification;

import concerta.core.EventType;

import java.time.Duration;

public interface EventFormatter {
    String format(EventType type, Duration duration);
}
