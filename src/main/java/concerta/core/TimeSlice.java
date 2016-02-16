package concerta.core;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static concerta.core.EventType.*;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static rx.Observable.*;

@SuppressWarnings("WeakerAccess")
public class TimeSlice {
    public static final TimeUnit UNIT = SECONDS;

    private Scheduler scheduler = Schedulers.immediate();
    private long inProgressPeriod = Integer.MAX_VALUE;
    private Collection<Integer> elapsesIn = Collections.emptyList();

    public TimeSlice() {
    }

    public TimeSlice(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public TimeSlice inProgressEvery(int period) {
        inProgressPeriod = period;
        return this;
    }

    public TimeSlice elapsesIn(Integer... times) {
        elapsesIn = asList(times);
        return this;
    }

    public Observable<Event> start(int duration) {
        return everyOneUnitUpTo(duration).concatMap(new ToEvent(duration));
    }

    private Observable<Long> everyOneUnitUpTo(int duration) {
        return interval(1, UNIT, scheduler)
            .map(t -> t + 1)
            .take(duration);
    }

    private class ToEvent implements Func1<Long, Observable<Event>>  {
        private final int duration;

        public ToEvent(int duration) {
            this.duration = duration;
        }

        @Override
        public Observable<Event> call(Long t) {
            if (t == 1) {
                return just(new Event(STARTING));
            }
            if (t == duration) {
                return just(new Event(ELAPSED));
            }
            if (willElapseSoon(t)) {
                return just(new Event(WILL_ELAPSE_SOON));
            }
            if (inProgress(t)) {
                return just(new Event(IN_PROGRESS));
            }
            return empty();
        }

        private boolean willElapseSoon(long t) {
            long timeToGo = duration - t;
            return elapsesIn.stream().anyMatch(e -> e == timeToGo);
        }

        private boolean inProgress(long t) {
            return t % inProgressPeriod == 0;
        }
    }
}
