package concerta.notification;

import concerta.core.Event;
import org.fusesource.jansi.Ansi;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.functions.Action1;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.List;

import static concerta.core.EventType.*;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@SuppressWarnings("resource")
public class ConsoleNotifierTest {
    @Test
    public void first_notified_event_can_be_a_tick() {
        notifier.call(Event.of(TICK, Duration.ZERO));

        assertThat(out.toString(), equalTo("TICK PT0S\n"));
    }

    @Test
    public void first_notified_event_can_be_a_milestone() {
        notifier.call(Event.of(STARTING, Duration.of(10, MINUTES)));
        assertThat(out.toString(), equalTo("STARTING PT10M\n"));
    }

    @Test
    public void last_milestone_event_is_retained_and_printed_together_with_tick() {
        notifyRetainingLastOutput(Event.of(TICK, Duration.ZERO),
            Event.of(STARTING, Duration.of(10, MINUTES)));
        assertThat(out.toString(), equalTo("TICK PT0S STARTING PT10M\n"));

        notifyRetainingLastOutput(Event.of(TICK, Duration.of(1, SECONDS)));
        assertThat(out.toString(), equalTo("TICK PT1S STARTING PT10M\n"));

        notifyRetainingLastOutput(Event.of(TICK, Duration.of(2, SECONDS)),
            Event.of(IN_PROGRESS, Duration.of(1, MINUTES)));
        assertThat(out.toString(), equalTo("TICK PT2S IN_PROGRESS PT1M\n"));
    }

    private void notifyRetainingLastOutput(Event... e) {
        List<Event> events = asList(e);
        events.subList(0, events.size() - 1).forEach(notifier::call);
        out.reset();
        events.subList(events.size() - 1, events.size()).forEach(notifier::call);
    }

    @Before
    public void setup() throws Exception {
        disableAnsiSequences();
        notifier = new ConsoleNotifier(formatter, new PrintStream(out, true, "UTF8"));
    }

    @After
    public void cleanup() {
        restoreAnsiSequences();
    }

    private boolean ansiEnabled;

    private void disableAnsiSequences() {
        ansiEnabled = Ansi.isEnabled();
        Ansi.setEnabled(false);
    }

    private void restoreAnsiSequences() {
        Ansi.setEnabled(ansiEnabled);
    }

    private EventMessageFormatter formatter = new FakeEventMessageFormatter();
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private Action1<Event> notifier;

    private static class FakeEventMessageFormatter extends EventMessageFormatter {
        @Override
        public String message(Event event) {
            return event.equals(Event.NULL) ? "" : event.getType() + " " + event.getDuration();
        }
    }
}