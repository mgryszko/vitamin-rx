import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.observables.ConnectableObservable;

import static java.util.concurrent.TimeUnit.MINUTES;

public class TimeSlice {
    public void start(int durationInMin, Iterable<Observer<Long>> observers, Scheduler scheduler) {
        ConnectableObservable<Long> timer = Observable.timer(durationInMin, MINUTES, scheduler).publish();
        observers.forEach(timer::subscribe);
        timer.connect();
    }
}
