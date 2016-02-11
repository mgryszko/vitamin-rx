package concerta;

import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import static concerta.Event.ELAPSED;
import static concerta.Event.IN_PROGRESS;
import static concerta.Event.STARTED;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TimeSliceTest {
    @Test
    public void elapse_time_slice_start_end_events() {
        Observable<Event> timeSlice = new TimeSlice().start(duration, scheduler);
        timeSlice.subscribe(eventObserver);

        scheduler.advanceTimeBy(duration, SECONDS);
        eventObserver.assertValues(STARTED, ELAPSED);
        eventObserver.assertCompleted();
    }

    @Test
    public void elapse_time_slice_in_progress_events() {
        int inProgressPeriod = 2;
        Observable<Event> timeSlice = new TimeSlice().start(duration, inProgressPeriod, scheduler);
        timeSlice.subscribe(eventObserver);

        scheduler.advanceTimeBy(2 * inProgressPeriod, SECONDS);
        eventObserver.assertValues(STARTED, IN_PROGRESS, IN_PROGRESS);
    }

    private int duration = 10;
    private TestScheduler scheduler = Schedulers.test();
    private TestSubscriber<Event> eventObserver = new TestSubscriber<>();
}
