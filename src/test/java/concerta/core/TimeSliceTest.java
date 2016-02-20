package concerta.core;

import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.time.Duration;

import static concerta.core.EventType.*;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;

public class TimeSliceTest {
    @Test
    public void elapse_time_slice_start_end_events() {
        Observable<Event> timeSlice = new TimeSlice(scheduler).start(duration);
        timeSlice.subscribe(eventObserver);

        eventsObservedAfter(duration,
            new Event(STARTING, Duration.of(duration, MINUTES)),
            new Event(ELAPSED, Duration.of(duration, MINUTES)));
        eventObserver.assertCompleted();
    }

    @Test
    public void in_progress_events_of_time_slice() {
        int inProgressPeriod = 2;
        Observable<Event> timeSlice = new TimeSlice(scheduler).inProgressEvery(inProgressPeriod).start(duration);
        timeSlice.subscribe(eventObserver);

        eventsObservedAfter(2 * inProgressPeriod,
            new Event(STARTING, Duration.of(duration, MINUTES)),
            new Event(IN_PROGRESS, Duration.of(inProgressPeriod, MINUTES)),
            new Event(IN_PROGRESS, Duration.of(2 * inProgressPeriod, MINUTES)));
    }

    @Test
    public void will_elapse_soon_events_before_time_slice_end() {
        Observable<Event> timeSlice = new TimeSlice(scheduler).elapsesIn(asList(5, 3, 1)).start(duration);
        timeSlice.subscribe(eventObserver);

        eventsObservedAfter(duration,
            new Event(STARTING, Duration.of(duration, MINUTES)),
            new Event(WILL_ELAPSE_SOON, Duration.of(5, MINUTES)),
            new Event(WILL_ELAPSE_SOON, Duration.of(3, MINUTES)),
            new Event(WILL_ELAPSE_SOON, Duration.of(1, MINUTES)),
            new Event(ELAPSED, Duration.of(duration, MINUTES)));
    }

    private final int duration = 10;
    private TestScheduler scheduler = Schedulers.test();
    private TestSubscriber<Event> eventObserver = new TestSubscriber<>();

    private void eventsObservedAfter(int time, Event... expectedEvents) {
        scheduler.advanceTimeBy(time, TimeSlice.DEFAULT_UNIT);
        eventObserver.assertValues(expectedEvents);
    }
}
