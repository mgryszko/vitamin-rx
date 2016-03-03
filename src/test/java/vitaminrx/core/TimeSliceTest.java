package vitaminrx.core;

import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static vitaminrx.core.EventType.*;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TimeSliceTest {
    private final Duration duration = Duration.of(10, MINUTES);
    private TestScheduler scheduler = Schedulers.test();
    private TestSubscriber<Event> eventObserver = new TestSubscriber<>();

    @Test
    public void elapse_time_slice_start_end_events() {
        Observable<Event> timeSlice = new TimeSlice(scheduler).start(duration);
        timeSlice.subscribe(eventObserver);

        scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        assertThat(eventObserver.getOnNextEvents(), equalTo(asList(
            Event.of(STARTING, duration),
            Event.of(TICK, Duration.of(1, SECONDS)),
            Event.of(TICK, Duration.of(2, SECONDS)),
            Event.of(TICK, Duration.of(3, SECONDS)))));

        scheduler.advanceTimeBy(duration.toMinutes(), TimeUnit.MINUTES);
        assertThat(lastEvents(4), equalTo(asList(
            Event.of(TICK, duration.minusSeconds(3)),
            Event.of(TICK, duration.minusSeconds(2)),
            Event.of(TICK, duration.minusSeconds(1)),
            Event.of(ELAPSED, duration))));
        eventObserver.assertCompleted();
    }

    @Test
    public void in_progress_events_of_time_slice() {
        Duration inProgressPeriod = Duration.of(2, MINUTES);
        Observable<Event> timeSlice = new TimeSlice(scheduler).inProgressEvery(inProgressPeriod).start(duration);
        timeSlice.subscribe(eventObserver);

        scheduler.advanceTimeBy(2 * inProgressPeriod.toMinutes(), TimeUnit.MINUTES);
        assertThat(allMilestoneEvents(), contains(
            Event.of(STARTING, duration),
            Event.of(IN_PROGRESS, inProgressPeriod),
            Event.of(IN_PROGRESS, inProgressPeriod.multipliedBy(2))));
    }

    @Test
    public void will_elapse_soon_events_before_time_slice_end() {
        List<Duration> times = asList(Duration.of(5, MINUTES), Duration.of(3, MINUTES), Duration.of(1, MINUTES));
        Observable<Event> timeSlice = new TimeSlice(scheduler).elapsesIn(times).start(duration);
        timeSlice.subscribe(eventObserver);

        scheduler.advanceTimeBy(duration.toMinutes(), TimeUnit.MINUTES);
        assertThat(allMilestoneEvents(), contains(
            Event.of(STARTING, duration),
            Event.of(WILL_ELAPSE_SOON, Duration.of(5, MINUTES)),
            Event.of(WILL_ELAPSE_SOON, Duration.of(3, MINUTES)),
            Event.of(WILL_ELAPSE_SOON, Duration.of(1, MINUTES)),
            Event.of(ELAPSED, duration)));
    }

    private List<Event> lastEvents(int n) {
        return observedEvents().skip(eventObserver.getOnNextEvents().size() - n).collect(toList());
    }

    private List<Event> allMilestoneEvents() {
        return observedEvents().filter(Event::isMilestone).collect(toList());
    }

    private Stream<Event> observedEvents() {
        return eventObserver.getOnNextEvents().stream();
    }
}
