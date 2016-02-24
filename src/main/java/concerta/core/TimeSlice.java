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
import static java.lang.Math.toIntExact;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static rx.Observable.interval;
import static rx.Observable.just;

public class TimeSlice {
    public static final TimeUnit DEFAULT_UNIT = TimeUnit.MINUTES;

    private Scheduler scheduler = Schedulers.immediate();
    private Duration inProgressPeriod = Duration.ZERO;
    private Collection<Integer> elapsesIn = emptyList();

    public TimeSlice() {
    }

    public TimeSlice(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public TimeSlice inProgressEvery(Duration period) {
        inProgressPeriod = period;
        return this;
    }

    public TimeSlice elapsesIn(List<Integer> times) {
        elapsesIn = new ArrayList<>(times);
        return this;
    }

    public Observable<Event> start(Duration duration) {
        return everyOneUnitUpTo(duration).concatMap(new ToEvent(duration));
    }

    private Observable<Integer> everyOneUnitUpTo(Duration duration) {
        return interval(0, 1, TimeUnit.SECONDS, scheduler)
            .map(Math::toIntExact)
            .take(toIntExact(duration.get(SECONDS) + 1));
    }

    private final class ToEvent implements Func1<Integer, Observable<Event>>  {
        private final Duration duration;

        private ToEvent(Duration duration) {
            this.duration = duration;
        }

        @Override
        public Observable<Event> call(Integer t) {
            if (t == 0) {
                return just(new Event(STARTING, duration));
            }
            if (t == duration.get(SECONDS)) {
                return just(new Event(ELAPSED, duration));
            }
            if (willElapseSoon(t)) {
                return just(new Event(WILL_ELAPSE_SOON, Duration.of(timeToGo(t), SECONDS)));
            }
            if (inProgress(t)) {
                return just(new Event(IN_PROGRESS, Duration.of(t / 60, MINUTES)));
            }
            return just(new Event(TICK, Duration.of(t, SECONDS  )));
        }

        private boolean willElapseSoon(long t) {
            return elapsesIn.stream().anyMatch(e -> e * 60 == timeToGo(t));
        }

        private long timeToGo(long t) {
            return duration.minusSeconds(t).get(SECONDS);
        }

        // TODO extension method of duration
        private boolean inProgress(long t) {
            if (inProgressPeriod.equals(Duration.ZERO)) return false;
            return t % inProgressPeriod.get(SECONDS) == 0;
        }
    }
}
