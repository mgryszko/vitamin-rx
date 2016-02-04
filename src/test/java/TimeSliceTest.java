import org.junit.Test;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.util.concurrent.TimeUnit;

public class TimeSliceTest {
    @Test
    public void elapse_time_slice_no_events() {
        int duration = 10;
        TestScheduler scheduler = Schedulers.test();
        TestSubscriber<Long> subscriber = new TestSubscriber<>();

        new TimeSlice().start(duration, subscriber, scheduler);

        scheduler.advanceTimeBy(duration, TimeUnit.MINUTES);
        subscriber.assertCompleted();
    }
}
