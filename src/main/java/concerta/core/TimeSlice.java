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
import static java.util.concurrent.TimeUnit.MINUTES;
import static rx.Observable.*;

public class TimeSlice {
    public static final TimeUnit DEFAULT_UNIT = MINUTES;

    private Scheduler scheduler = Schedulers.immediate();
    private TimeUnit unit = DEFAULT_UNIT;
    private long inProgressPeriod = Integer.MAX_VALUE;
    private Collection<Integer> elapsesIn = Collections.emptyList();

    public TimeSlice() {
    }

    public TimeSlice(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public TimeSlice timeUnit(TimeUnit unit) {
        this.unit = unit;
        return this;
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

    private Observable<Integer> everyOneUnitUpTo(int duration) {
        return interval(0, 1, unit, scheduler)
            .map(Math::toIntExact)
            .take(duration + 1);
    }

    private class ToEvent implements Func1<Integer, Observable<Event>>  {
        private final int duration;

        public ToEvent(int duration) {
            this.duration = duration;
        }

        @Override
        public Observable<Event> call(Integer t) {
            if (t == 0) {
                return just(new Event(STARTING, duration));
            }
            if (t == duration) {
                return just(new Event(ELAPSED, duration));
            }
            if (willElapseSoon(t)) {
                return just(new Event(WILL_ELAPSE_SOON, t));
            }
            if (inProgress(t)) {
                return just(new Event(IN_PROGRESS, t));
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
