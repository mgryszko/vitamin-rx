package concerta;

import rx.Observable;
import rx.Scheduler;

import static concerta.Event.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static rx.Observable.*;

public class TimeSlice {
    public Observable<Event> start(int durationInSeconds, Scheduler scheduler) {
        return start(durationInSeconds, Integer.MAX_VALUE, scheduler);
    }

    public Observable<Event> start(int durationInSeconds, int i, Scheduler scheduler) {
        return interval(1, SECONDS, scheduler).map(t -> t + 1).take(durationInSeconds).concatMap(t -> {
            if (t == 1) {
                return just(STARTED);
            }
            if (t % i == 0) {
                return just(IN_PROGRESS);
            }
            if (t == durationInSeconds) {
                return just(ELAPSED);
            }
            return empty();
        });
    }
}
