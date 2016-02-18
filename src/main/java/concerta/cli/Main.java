package concerta.cli;

import concerta.core.TimeSlice;
import concerta.notification.ConsoleNotifier;
import concerta.notification.EventMessageFormatter;
import concerta.notification.GrowlNotifier;
import org.kohsuke.args4j.*;

import java.util.concurrent.TimeUnit;

@SuppressWarnings({"UtilityClass", "UseOfSystemOutOrSystemErr", "FeatureEnvy", "CallToSystemExit"})
public final class Main {
    @Option(name = "-p", aliases = "--progress", usage = "progress notification period")
    private int inProgressPeriod = Integer.MAX_VALUE;

    @Option(name = "-e", aliases = "--elapse", usage = "elapses in", handler = MultiIntOptionHandler.class)
    private Integer[] elapsesIn;

    @Option(name = "--seconds", hidden = true)
    private boolean useSeconds;

    @Argument(required = true, usage = "duration")
    private int duration;

    private Main() {
    }

    public static void main(String... args) {
        new Main().doMain(args);
    }

    @SuppressWarnings("LawOfDemeter")
    private void doMain(String... args) {
        new ArgsParser(this, System.err).parse(args);

        EventMessageFormatter formatter = new EventMessageFormatter();
        TimeUnit timeUnit = useSeconds ? TimeUnit.SECONDS : TimeSlice.DEFAULT_UNIT;
        new TimeSlice()
            .timeUnit(timeUnit)
            .inProgressEvery(inProgressPeriod)
            .elapsesIn(elapsesIn)
            .start(duration)
            .doOnNext(new ConsoleNotifier(formatter, System.out))
            .doOnNext(new GrowlNotifier(formatter, System.err))
            .subscribe();
    }
}
