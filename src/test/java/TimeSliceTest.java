import org.junit.Test;
import rx.Observer;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class TimeSliceTest {
    @Test
    public void elapse_time_slice_no_events() {
        TestSubscriber<Long> observer1 = new TestSubscriber<>();
        TestSubscriber<Long> observer2 = new TestSubscriber<>();
        List<Observer<Long>> observers = asList(observer1, observer2);

        new TimeSlice().start(duration, observers, scheduler);

        scheduler.advanceTimeBy(duration, TimeUnit.MINUTES);
        observer1.assertCompleted();
        observer2.assertCompleted();
    }

    private int duration = 10;
    private TestScheduler scheduler = Schedulers.test();
}
