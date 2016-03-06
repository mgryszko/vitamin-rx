package vitaminrx.core;

import org.junit.Test;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.LongStream.rangeClosed;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static vitaminrx.core.EventType.*;

public class TimeSliceTest {
    private final Duration duration = Duration.of(10, MINUTES);
    private TestScheduler scheduler = Schedulers.test();
    private TestSubscriber<Event> eventObserver = new TestSubscriber<>();

    @Test
    public void elapse_time_slice_start_end_events() {
        new TimeSlice(scheduler).start(duration).subscribe(eventObserver);

        scheduler.advanceTimeBy(duration.toMinutes(), TimeUnit.MINUTES);
        assertThat(allMilestoneEvents(), equalTo(asList(
            Event.of(STARTING, duration),
            Event.of(ELAPSED, duration))));
        eventObserver.assertCompleted();
    }

    @Test
    public void in_progress_events_of_time_slice() {
        Duration inProgressPeriod = Duration.of(2, MINUTES);
        new TimeSlice(scheduler).inProgressEvery(inProgressPeriod).start(duration).subscribe(eventObserver);

        scheduler.advanceTimeBy(2 * inProgressPeriod.toMinutes(), TimeUnit.MINUTES);
        assertThat(allMilestoneEvents(), contains(
            Event.of(STARTING, duration),
            Event.of(IN_PROGRESS, inProgressPeriod),
            Event.of(IN_PROGRESS, inProgressPeriod.multipliedBy(2))));
    }

    @Test
    public void will_elapse_soon_events_before_time_slice_end() {
        List<Duration> times = asList(Duration.of(5, MINUTES), Duration.of(3, MINUTES), Duration.of(1, MINUTES));
        new TimeSlice(scheduler).elapsesIn(times).start(duration).subscribe(eventObserver);

        scheduler.advanceTimeBy(duration.toMinutes(), TimeUnit.MINUTES);
        assertThat(allMilestoneEvents(), contains(
            Event.of(STARTING, duration),
            Event.of(WILL_ELAPSE_SOON, Duration.of(5, MINUTES)),
            Event.of(WILL_ELAPSE_SOON, Duration.of(3, MINUTES)),
            Event.of(WILL_ELAPSE_SOON, Duration.of(1, MINUTES)),
            Event.of(ELAPSED, duration)));
    }

    @Test
    public void tick_events() {
        new TimeSlice(scheduler).start(duration).subscribe(eventObserver);

        scheduler.advanceTimeBy(duration.toMinutes(), TimeUnit.MINUTES);
        assertThat(allTickEvents(), equalTo(tickSeq(duration, Duration.ZERO)));
    }

    @Test
    public void can_be_paused_and_resumed() {
        TimeSlice timeSlice = new TimeSlice(scheduler);
        timeSlice.start(duration).subscribe(eventObserver);

        scheduler.advanceTimeBy(1, TimeUnit.MINUTES);
        timeSlice.toggle();
        scheduler.advanceTimeBy(1, TimeUnit.MINUTES);

        assertThat(allTickEvents(), equalTo(tickSeq(duration, duration.minus(Duration.of(1, MINUTES)))));

        timeSlice.toggle();
        scheduler.advanceTimeBy(1, TimeUnit.MINUTES);

        assertThat(allTickEvents(), equalTo(tickSeq(duration, duration.minus(Duration.of(2, MINUTES)))));
    }

    private List<Event> allMilestoneEvents() {
        return observedEvents().filter(Event::isMilestone).collect(toList());
    }

    private List<Event> allTickEvents() {
        return observedEvents().filter(Event::isTick).collect(toList());
    }

    private Stream<Event> observedEvents() {
        return eventObserver.getOnNextEvents().stream();
    }

    private List<Event> tickSeq(Duration last, Duration first) {
        List<Event> events = rangeClosed(first.getSeconds(), last.getSeconds())
            .mapToObj(t -> Event.of(TICK, Duration.of(t, SECONDS)))
            .collect(toList());
        Collections.reverse(events);
        return events;
    }
}
