package vitaminrx.notification;

import vitaminrx.core.Event;
import vitaminrx.core.EventType;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import static vitaminrx.core.EventType.NULL;
import static java.util.function.Predicate.isEqual;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class EventMessageFormatterTest {
    private EventFormatter formatter = new EventMessageFormatter();

    @Test
    @Parameters(method = "supportedEventTypes")
    public void formats_event_duration(EventType eventType) {
        String message = Event.of(eventType, Duration.parse("PT1M2S")).format(formatter);

        assertThat(message, containsString("1:02"));
    }

    @SuppressWarnings("unused")
    private List<EventType> supportedEventTypes() {
        return Stream.of(EventType.values()).filter(isEqual(NULL).negate()).collect(toList());
    }

    @Test
    public void returns_empty_string_for_null_events() {
        assertThat(Event.NULL.format(formatter), equalTo(""));
    }
}