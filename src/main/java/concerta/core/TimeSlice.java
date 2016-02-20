package concerta.core;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static concerta.core.EventType.*;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Collections.emptyList;
import static rx.Observable.*;

public class TimeSlice {
    public static final TimeUnit DEFAULT_UNIT = TimeUnit.MINUTES;

    private Scheduler scheduler = Schedulers.immediate();
    private TimeUnit timeUnit = DEFAULT_UNIT;
    private long inProgressPeriod = Integer.MAX_VALUE;
    private Collection<Integer> elapsesIn = emptyList();

    public TimeSlice() {
    }

    public TimeSlice(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public TimeSlice timeUnit(TimeUnit unit) {
        timeUnit = unit;
        return this;
    }

    public TimeSlice inProgressEvery(int period) {
        inProgressPeriod = period;
        return this;
    }

    public TimeSlice elapsesIn(List<Integer> times) {
        elapsesIn = new ArrayList<>(times);
        return this;
    }

    public Observable<Event> start(int duration) {
        return everyOneUnitUpTo(duration).concatMap(new ToEvent(duration));
    }

    private Observable<Integer> everyOneUnitUpTo(int duration) {
        return interval(0, 1, timeUnit, scheduler)
            .map(Math::toIntExact)
            .take(duration + 1);
    }

    private final class ToEvent implements Func1<Integer, Observable<Event>>  {
        private final int duration;

        private ToEvent(int duration) {
            this.duration = duration;
        }

        @Override
        public Observable<Event> call(Integer t) {
            if (t == 0) {
                return just(new Event(STARTING, Duration.of(duration, MINUTES)));
            }
            if (t == duration) {
                return just(new Event(ELAPSED, Duration.of(duration, MINUTES)));
            }
            if (willElapseSoon(t)) {
                return just(new Event(WILL_ELAPSE_SOON, Duration.of(timeToGo(t), MINUTES)));
            }
            if (inProgress(t)) {
                return just(new Event(IN_PROGRESS, Duration.of(t, MINUTES)));
            }
            return empty();
        }

        private boolean willElapseSoon(long t) {
            return elapsesIn.stream().anyMatch(e -> e == timeToGo(t));
        }

        private long timeToGo(long t) {
            return duration - t;
        }

        private boolean inProgress(long t) {
            return t % inProgressPeriod == 0;
        }
    }
}
