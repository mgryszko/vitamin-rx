package vitaminrx.core;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static rx.Observable.interval;
import static rx.Observable.just;
import static vitaminrx.core.EventType.*;

public class TimeSlice {
    private Scheduler scheduler = Schedulers.immediate();
    private Duration inProgressPeriod = Duration.ZERO;
    private List<Duration> elapsesIn = emptyList();
    private final AtomicLong counter = new AtomicLong();
    private final AtomicBoolean paused = new AtomicBoolean();

    public TimeSlice() {
    }

    public TimeSlice(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public TimeSlice inProgressEvery(Duration period) {
        inProgressPeriod = period;
        return this;
    }

    public TimeSlice elapsesIn(List<Duration> times) {
        elapsesIn = Collections.unmodifiableList(times);
        return this;
    }

    public Observable<Event> start(Duration duration) {
        return pausableInterval()
            .map(t -> Duration.of(counter.getAndIncrement(), SECONDS))
            .takeUntil(t -> t.equals(duration))
            .concatMap(new ToEvent(duration));
    }

    private Observable<Long> pausableInterval() {
        return interval(0, 1, TimeUnit.SECONDS, scheduler)
            .filter(t -> !paused.get());
    }

    public void toggle() {
        paused.set(!paused.get());
    }

    private final class ToEvent implements Func1<Duration, Observable<Event>>  {
        private final Duration duration;

        private ToEvent(Duration duration) {
            this.duration = duration;
        }

        @Override
        public Observable<Event> call(Duration t) {
            Event tick = Event.of(TICK, duration.minus(t));
            if (t.equals(Duration.ZERO)) {
                return just(tick, Event.of(STARTING, duration));
            }
            if (t.equals(duration)) {
                return just(tick, Event.of(ELAPSED, duration));
            }
            if (willElapseSoon(t)) {
                return just(tick, Event.of(WILL_ELAPSE_SOON, timeToGo(t)));
            }
            if (inProgress(t)) {
                return just(tick, Event.of(IN_PROGRESS, t));
            }
            return just(tick);
        }

        private boolean willElapseSoon(Duration t) {
            return elapsesIn.contains(timeToGo(t));
        }

        private Duration timeToGo(Duration t) {
            return duration.minus(t);
        }

        private boolean inProgress(Duration t) {
            return isInProgressPeriodConfigured() && isMultipleOf(t, inProgressPeriod);
        }

        private boolean isInProgressPeriodConfigured() {
            return !inProgressPeriod.equals(Duration.ZERO);
        }

        private boolean isMultipleOf(Duration d1, Duration d2) {
            return d1.getSeconds() % d2.getSeconds() == 0;
        }
    }
}
