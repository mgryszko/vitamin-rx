package concerta;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

@SuppressWarnings({"ClassNamingConvention", "UtilityClass"})
public final class Main {
    private Main() {
    }

    public static void main(String... args) {
        Action1<Event> display = System.out::println;
        new TimeSlice()
            .start(Integer.valueOf(args[0]), Schedulers.immediate())
            .doOnNext(display)
            .subscribe();
    }
}
