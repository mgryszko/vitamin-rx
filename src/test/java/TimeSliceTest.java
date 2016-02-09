import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.util.concurrent.TimeUnit;

public class TimeSliceTest {
    @Test
    public void elapse_time_slice_start_end_event() {
        TestSubscriber<Event> eventObserver = new TestSubscriber<>();

        Observable<Event> timeSlice = new TimeSlice().start(duration, scheduler);

        timeSlice.subscribe(eventObserver);
        scheduler.advanceTimeBy(duration, TimeUnit.SECONDS);
        eventObserver.assertValues(Event.STARTED, Event.ELAPSED);
        eventObserver.assertCompleted();
    }

    private int duration = 10;
    private TestScheduler scheduler = Schedulers.test();
}
