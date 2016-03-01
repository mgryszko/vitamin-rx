package concerta.cli;

import concerta.core.TimeSlice;
import concerta.notification.ConsoleNotifier;
import concerta.notification.EventMessageFormatter;
import concerta.notification.GrowlNotifier;
import org.fusesource.jansi.AnsiConsole;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"UtilityClass", "UseOfSystemOutOrSystemErr", "FeatureEnvy", "CallToSystemExit"})
public final class Main {
    @Option(name = "-p", aliases = "--progress", usage = "progress notification period",
        handler = SimplifiedDurationHandler.class)
    private Duration inProgressPeriod = Duration.ZERO;

    @Option(name = "-e", aliases = "--elapse", usage = "elapses in", handler = MultiDurationOptionHandler.class)
    private List<Duration> elapsesIn = new ArrayList<>();

    @Argument(required = true, usage = "duration", metaVar = "duration", handler = SimplifiedDurationHandler.class)
    private Duration duration;

    private Main() {
    }

    public static void main(String... args) {
        new Main().doMain(args);
    }

    @SuppressWarnings("LawOfDemeter")
    private void doMain(String... args) {
        new ArgsParser(this, System.err).parse(args);
        AnsiConsole.systemInstall();

        EventMessageFormatter formatter = new EventMessageFormatter();
        new TimeSlice()
            .inProgressEvery(inProgressPeriod)
            .elapsesIn(elapsesIn)
            .start(duration)
            .doOnNext(new ConsoleNotifier(formatter, System.out))
            .doOnNext(new GrowlNotifier(formatter, System.err))
            .subscribe();
    }
}
