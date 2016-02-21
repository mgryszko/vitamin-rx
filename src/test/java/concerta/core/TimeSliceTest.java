package concerta.core;

import org.hamcrest.Matchers;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static concerta.core.EventType.*;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TimeSliceTest {
    @Test
    public void elapse_time_slice_start_end_events() {
        Observable<Event> timeSlice = new TimeSlice(scheduler).start(duration);
        timeSlice.subscribe(eventObserver);

        scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        assertThat(eventObserver.getOnNextEvents(), equalTo(asList(
            new Event(STARTING, duration),
            new Event(TICK, Duration.of(1, SECONDS)),
            new Event(TICK, Duration.of(2, SECONDS)),
            new Event(TICK, Duration.of(3, SECONDS))
        )));

        scheduler.advanceTimeBy(duration.toMinutes(), TimeUnit.MINUTES);
        List<Event> lastEvents = eventObserver.getOnNextEvents().stream().skip(eventObserver.getOnNextEvents().size() - 4).collect(toList());
        assertThat(lastEvents, equalTo(asList(
            new Event(TICK, duration.minusSeconds(3)),
            new Event(TICK, duration.minusSeconds(2)),
            new Event(TICK, duration.minusSeconds(1)),
            new Event(ELAPSED, duration)
        )));
        eventObserver.assertCompleted();
    }

    @Test
    public void in_progress_events_of_time_slice() {
        int inProgressPeriod = 2;
        Observable<Event> timeSlice = new TimeSlice(scheduler).inProgressEvery(inProgressPeriod).start(duration);
        timeSlice.subscribe(eventObserver);

        scheduler.advanceTimeBy(2 * inProgressPeriod, TimeUnit.MINUTES);
        assertThat(eventObserver.getOnNextEvents().stream().filter(e -> e.getType() != TICK).collect(toList()), Matchers.contains(
            new Event(STARTING, duration),
            new Event(IN_PROGRESS, Duration.of(inProgressPeriod, MINUTES)),
            new Event(IN_PROGRESS, Duration.of(2 * inProgressPeriod, MINUTES))
        ));
    }

    @Test
    public void will_elapse_soon_events_before_time_slice_end() {
        Observable<Event> timeSlice = new TimeSlice(scheduler).elapsesIn(asList(5, 3, 1)).start(duration);
        timeSlice.subscribe(eventObserver);

        scheduler.advanceTimeBy(duration.toMinutes(), TimeUnit.MINUTES);
        assertThat(eventObserver.getOnNextEvents().stream().filter(e -> e.getType() != TICK).collect(toList()), Matchers.contains(
            new Event(STARTING, duration),
            new Event(WILL_ELAPSE_SOON, Duration.of(5, MINUTES)),
            new Event(WILL_ELAPSE_SOON, Duration.of(3, MINUTES)),
            new Event(WILL_ELAPSE_SOON, Duration.of(1, MINUTES)),
            new Event(ELAPSED, duration)
        ));
    }

    private final Duration duration = Duration.of(10, MINUTES);
    private TestScheduler scheduler = Schedulers.test();
    private TestSubscriber<Event> eventObserver = new TestSubscriber<>();

    private void eventsObservedAfter(int time, Event... expectedEvents) {
        scheduler.advanceTimeBy(time, TimeSlice.DEFAULT_UNIT);
        eventObserver.assertValues(expectedEvents);
    }
}
