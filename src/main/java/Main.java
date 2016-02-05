import rx.Observer;
import rx.functions.Action1;
import rx.observers.Observers;
import rx.schedulers.Schedulers;

import static java.util.Arrays.asList;

@SuppressWarnings("ClassNamingConvention")
public final class Main {
    private Main() {
    }

    public static void main(String... args) {
        Observer<Long> display = Observers.create(t -> System.out.println("visual notification"));
        Observer<Long> sound = Observers.create(t -> System.out.println("sound notification"));
        new TimeSlice().start(
            Integer.valueOf(args[0]),
            asList(display, sound),
            Schedulers.immediate());
    }
}
