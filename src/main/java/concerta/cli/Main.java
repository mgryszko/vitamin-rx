package concerta.cli;

import concerta.core.EventType;
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
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.MINUTES;

@SuppressWarnings({"UtilityClass", "UseOfSystemOutOrSystemErr", "FeatureEnvy", "CallToSystemExit"})
public final class Main {
    @Option(name = "-p", aliases = "--progress", usage = "progress notification period",
        handler = SimplifiedDurationHandler.class)
    private Duration inProgressPeriod = Duration.ZERO;

    @Option(name = "-e", aliases = "--elapse", usage = "elapses in", handler = MultiIntOptionHandler.class)
    private List<Integer> elapsesIn = new ArrayList<>();

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
            .elapsesIn(elapsesIn.stream().map(t -> Duration.of(t, MINUTES)).collect(Collectors.toList()))
            .start(duration)
            .filter(event -> event.getType() != EventType.TICK)
            .doOnNext(new ConsoleNotifier(formatter, System.out))
            .doOnNext(new GrowlNotifier(formatter, System.err))
            .subscribe();
    }
}
