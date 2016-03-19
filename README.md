# Vitamin-Rx

Simplified clone of [Vitamin-R 2](http://www.publicspace.net/Vitamin-R/), made for exploring [Reactive Extensions](http://reactivex.io/)/[RxJava](https://github.com/ReactiveX/RxJava).

As the original piece of software, it works with short periods of highly concentrated time - time slices. In contrast to Vitamin-R, it is implemented as a CLI application. The functionality is very reduced - it works as a countdown timer with notifications displayed in the terminal and via Growl (hence it works only on MacOSX).

## Usage

Compile and package the application:
```bash
./gradlew shadowJar
```

Start the time slice:
```bash
java -jar build/libs/vitamin-rx-all.jar [-p|--progress] [-e|--elapse] duration
```

**Options:**

`duration N|Nm|Ns` - duration of the time slice

`-p N|Nm|Ns, --progress=N|Nm|Ns` - in-progress notifications, periodic every N minutes/seconds

`-e N|Nm|Ns, --elapse=N|Nm|Ns[,N|Nm|Ns,...]` - will elapse soon notifications, N minutes before the time slice elapses. Multiple duration values separated by comma.

**Duration format by example:**

`N|N[m|M]|N[s|S]`:
* 5 - 5 minutes (minute is the default unit)
* 10m - 10 minutes
* 65s - 65 seconds

Unit symbol is case insensitive (`m` is same as `M`, `s` as `S`)

**Examples:**

Typical time slice of 20 minutes. In-progress notifications every 2 minutes. Will elapse soon notifications 5, 3 and 1 minutes before the time slice end.
```bash
java -jar build/libs/vitamin-rx-all.jar --progress=2 --elapse=5,3,1 20
```

Quick test - 20 seconds time slice:
```bash
java -jar build/libs/vitamin-rx-all.jar --progress=4s --elapse=10s,5s,2s 20s
```

## How it works

The central point of the application is the [TimeSlice](src/main/java/vitaminrx/core/TimeSlice.java) class. It is a builder for an [Observable](http://reactivex.io/documentation/observable.html) stream of [Event](src/main/java/vitaminrx/core/Event.java).

It starts creating the event stream from a simple unbounded `interval` Observable, emitting a `long` tick every second. The ticks are transformed into `java.time.Duration` and the stream is truncated to the duration of the time slice. Finally, ticks expressed as `Duration` are transformed into meaningful domain events.

The transformations leading to the stream of `Event` are side-effect free. Actions on the `Observable` with side effects are added via a `do*` method (`doOnNext`) in the `Main` class. In this way I easily can unit test the `Observable` produced by `TimeSlice`.

For testing I used `rx.schedulers.TestScheduler`, which allows you to play with time (i.e. advance it by a number of time units).

## Further extensions

I played a bit with pausing an interval `Observable`, but finally I didn't make the code "production" ready. You can find the sketch of the solution on [Stack Overflow](http://stackoverflow.com/questions/35782767/how-can-an-observable-be-paused-without-loosing-the-items-emitted).
