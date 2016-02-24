package concerta.cli;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class SimplifiedDurationHandlerTest {
    @Option(name = "-d", aliases = "--duration", handler = SimplifiedDurationHandler.class)
    private Duration duration;

    @Test
    @Parameters(method = "parseableDurations")
    public void parses_option_as_duration(String[] args, Duration expected) throws CmdLineException {
        new CmdLineParser(this).parseArgument(args);

        assertThat(duration, equalTo(expected));
    }

    @SuppressWarnings("unused")
    private Object[] parseableDurations() {
        return new Object[] {
            new Object[] {new String[] {"-d", "3"}, Duration.of(3, MINUTES)},
            new Object[] {new String[] {"-d", "3m"}, Duration.of(3, MINUTES)},
            new Object[] {new String[] {"-d", "3s"}, Duration.of(3, SECONDS)},
            new Object[] {new String[] {"--duration=3"}, Duration.of(3, MINUTES)},
            new Object[] {new String[] {"--duration=3s"}, Duration.of(3, SECONDS)}
        };
    }

    @Test
    @Parameters(method = "misformattedDurations")
    public void parse_error_on_wrong_duration_format(String[] args, Matcher<String> check) {
        try {
            new CmdLineParser(this).parseArgument(args);
        } catch (CmdLineException e) {
            assertThat(e.getMessage(), check);
        }
    }

    @SuppressWarnings("unused")
    private Object[] misformattedDurations() {
        return new Object[] {
            new Object[] {new String[] {"-d", "1h"}, allOf(containsString("-d"), containsString("1h"))},
            new Object[] {new String[] {"--duration=1h"}, allOf(containsString("--duration"), containsString("1h"))},
            new Object[] {new String[] {"-d", "x"}, allOf(containsString("-d"), containsString("x"))},
            new Object[] {new String[] {"--duration=x"}, allOf(containsString("--duration"), containsString("x"))}
        };
    }
}
