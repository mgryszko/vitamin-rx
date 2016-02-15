package concerta.cli;

import concerta.core.TimeSlice;
import concerta.notification.ConsoleNotifier;
import concerta.notification.EventMessageFormatter;
import concerta.notification.GrowlNotifier;
import org.kohsuke.args4j.*;

@SuppressWarnings({"UtilityClass", "UseOfSystemOutOrSystemErr", "FeatureEnvy", "CallToSystemExit"})
public final class Main {
    @Option(name = "-p", aliases = "--progress", usage = "progress notification period")
    private int inProgressPeriod = Integer.MAX_VALUE;

    @Option(name = "-e", aliases = "--elapse", usage = "elapses in", handler = MultiIntOptionHandler.class)
    private Integer[] elapsesIn;

    @Argument(required = true, usage = "duration")
    private int duration;

    private Main() {
    }

    public static void main(String... args) {
        new Main().doMain(args);
    }

    @SuppressWarnings("LawOfDemeter")
    private void doMain(String... args) {
        parseArgs(args);

        EventMessageFormatter formatter = new EventMessageFormatter();
        new TimeSlice()
            .inProgressEvery(inProgressPeriod)
            .elapsesIn(elapsesIn)
            .start(duration)
            .doOnNext(new ConsoleNotifier(formatter))
            .doOnNext(new GrowlNotifier(formatter))
            .subscribe();
    }

    private void parseArgs(String... args) {
        CmdLineParser parser = createCmdLineParser();
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            printErrorAndUsage(parser, e);
            System.exit(1);
        }
    }

    private CmdLineParser createCmdLineParser() {
        ParserProperties parserProperties = ParserProperties.defaults()
            .withShowDefaults(false)
            .withOptionValueDelimiter("=");
        return new CmdLineParser(this, parserProperties);
    }

    private void printErrorAndUsage(CmdLineParser parser, CmdLineException e) {
        System.err.println(e.getMessage());
        System.err.println();
        parser.printUsage(System.err);
    }
}
