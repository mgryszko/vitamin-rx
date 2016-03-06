package vitaminrx.cli;

import rx.Scheduler;
import rx.schedulers.Schedulers;
import vitaminrx.core.Event;
import vitaminrx.core.TimeSlice;
import vitaminrx.notification.ConsoleNotifier;
import vitaminrx.notification.EventFormatter;
import vitaminrx.notification.EventMessageFormatter;
import vitaminrx.notification.GrowlNotifier;
import org.fusesource.jansi.AnsiConsole;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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

        EventFormatter formatter = new EventMessageFormatter();
        Scheduler scheduler = Schedulers.newThread();
        TimeSlice timeSlice = new TimeSlice(scheduler)
            .inProgressEvery(inProgressPeriod)
            .elapsesIn(elapsesIn);
        timeSlice.start(duration)
            .doOnNext(new ConsoleNotifier(formatter, System.out))
            .filter(Event::isMilestone)
            .doOnNext(new GrowlNotifier(formatter, System.err))
            .subscribe();

        // TODO Tests work, CLI doesn't
        try {
            new Console(new KeyReceiver(timeSlice)).read(new InputStreamReader(System.in, Charset.forName("UTF-8")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
