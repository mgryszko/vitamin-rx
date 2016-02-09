import rx.Observable;
import rx.Scheduler;

import static java.util.concurrent.TimeUnit.SECONDS;
import static rx.Observable.empty;
import static rx.Observable.interval;
import static rx.Observable.just;

public class TimeSlice {
    public Observable<Event> start(int durationInSeconds, Scheduler scheduler) {
        return interval(1, SECONDS, scheduler).take(durationInSeconds).concatMap(t -> {
            if (t == 0) {
                return just(Event.STARTED);
            }
            if (t == (durationInSeconds - 1)) {
                return just(Event.ELAPSED);
            }
            return empty();
        });
    }
}
