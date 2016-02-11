package concerta;

import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import static concerta.Event.ELAPSED;
import static concerta.Event.STARTED;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TimeSliceTest {
    @Test
    public void elapse_time_slice_start_end_events() {
        TestSubscriber<Event> eventObserver = new TestSubscriber<>();

        Observable<Event> timeSlice = new TimeSlice().start(duration, scheduler);

        timeSlice.subscribe(eventObserver);
        scheduler.advanceTimeBy(duration, SECONDS);
        eventObserver.assertValues(STARTED, ELAPSED);
        eventObserver.assertCompleted();
    }

    private int duration = 10;
    private TestScheduler scheduler = Schedulers.test();
}
