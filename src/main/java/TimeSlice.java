import rx.Observable;
import rx.Observer;
import rx.Scheduler;

import static java.util.concurrent.TimeUnit.MINUTES;

public class TimeSlice {
    public void start(int durationInMin, Observer<? super Long> observer, Scheduler scheduler) {
        Observable.timer(durationInMin, MINUTES, scheduler).subscribe(observer);
    }
}
