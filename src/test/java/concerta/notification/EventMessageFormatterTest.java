package concerta.notification;

import concerta.core.Event;
import concerta.core.EventType;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import static concerta.core.EventType.TICK;
import static java.util.function.Predicate.isEqual;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class EventMessageFormatterTest {
    @Test
    @Parameters(method = "supportedEventTypes")
    // TODO Replace with @Parameters(source = EventType.class) when all event types are supported
    public void formats_event_duration(EventType eventType) {
        String message = formatter.message(new Event(eventType, Duration.parse("PT1M2S")));

        assertThat(message, Matchers.containsString("1:02"));
    }

    private List<EventType> supportedEventTypes() {
        return Stream.of(EventType.values()).filter(isEqual(TICK).negate()).collect(toList());
    }

    private EventMessageFormatter formatter = new EventMessageFormatter();
}