import rx.observers.Observers;
import rx.schedulers.Schedulers;

@SuppressWarnings("ClassNamingConvention")
public final class Main {
    private Main() {
    }

    public static void main(String... args) {
        new TimeSlice().start(
            Integer.valueOf(args[0]),
            Observers.create(l -> System.out.println("end")),
            Schedulers.immediate());
    }
}
