import org.junit.Test;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.util.concurrent.TimeUnit;

public class TimeSliceTest {
    // Events:
    // started
    // in progress - periodic event
    // will elapse soon - repeated event
    //   warn me: 10, 5, 3, 1 minutes before time elapses
    // has elapsed
    // was paused
    // was stopped
    // timed break
    // is started
    // is in progress
    // has elapsed

    // actions: play sound, speak, display (notify), dim screen
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
