package vitaminrx.cli;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.args4j.Argument;
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
    public static final String ARG_NAME = "ARG_NAME";
    private DurationOption option = new DurationOption();
    private DurationArgument argument = new DurationArgument();

    @Test
    @Parameters(method = "parseableOptions")
    public void parses_option_as_duration(String[] args, Duration expected) throws CmdLineException {
        new CmdLineParser(option).parseArgument(args);

        assertThat(option.duration, equalTo(expected));
    }

    @SuppressWarnings("unused")
    private Object[] parseableOptions() {
        return new Object[] {
            new Object[] {new String[] {"-d", "123"}, Duration.of(123, MINUTES)},
            new Object[] {new String[] {"-d", "123m"}, Duration.of(123, MINUTES)},
            new Object[] {new String[] {"-d", "123s"}, Duration.of(123, SECONDS)},
            new Object[] {new String[] {"--duration=123"}, Duration.of(123, MINUTES)},
            new Object[] {new String[] {"--duration=123S"}, Duration.of(123, SECONDS)},
            new Object[] {new String[] {"--duration=123M"}, Duration.of(123, MINUTES)}
        };
    }

    @Test
    @Parameters(method = "parseableArguments")
    public void parses_argument_as_duration(String[] args, Duration expected) throws CmdLineException {
        new CmdLineParser(argument).parseArgument(args);

        assertThat(argument.duration, equalTo(expected));
    }

    @SuppressWarnings("unused")
    private Object[] parseableArguments() {
        return new Object[] {
            new Object[] {new String[] {"123"}, Duration.of(123, MINUTES)},
            new Object[] {new String[] {"123m"}, Duration.of(123, MINUTES)},
            new Object[] {new String[] {"123s"}, Duration.of(123, SECONDS)}
        };
    }

    @Test
    @Parameters(method = "misformattedOptions")
    public void parse_error_on_wrong_duration_format_in_option(String[] args, Matcher<String> check) {
        try {
            new CmdLineParser(option).parseArgument(args);
        } catch (CmdLineException e) {
            assertThat(e.getMessage(), check);
        }
    }

    @SuppressWarnings("unused")
    private Object[] misformattedOptions() {
        return new Object[] {
            new Object[] {new String[] {"-d", "1h"}, allOf(containsString("-d"), containsString("1h"))},
            new Object[] {new String[] {"--duration=1h"}, allOf(containsString("--duration"), containsString("1h"))},
            new Object[] {new String[] {"-d", "x"}, allOf(containsString("-d"), containsString("x"))},
            new Object[] {new String[] {"--duration=x"}, allOf(containsString("--duration"), containsString("x"))}
        };
    }

    @Test
    @Parameters(method = "misformattedArguments")
    public void parse_error_on_wrong_duration_format_in_argument(String[] args, Matcher<String> check) {
        try {
            new CmdLineParser(argument).parseArgument(args);
        } catch (CmdLineException e) {
            assertThat(e.getMessage(), check);
        }
    }

    @SuppressWarnings("unused")
    private Object[] misformattedArguments() {
        return new Object[] {
            new Object[] {new String[] {"1h"}, allOf(containsString(ARG_NAME), containsString("1h"))},
            new Object[] {new String[] {"x"}, allOf(containsString(ARG_NAME), containsString("x"))}
        };
    }

    private static class DurationOption {
        @Option(name = "-d", aliases = "--duration", handler = SimplifiedDurationHandler.class)
        public Duration duration;
    }

    public static class DurationArgument {
        @Argument(required = true, metaVar = ARG_NAME, handler = SimplifiedDurationHandler.class)
        public Duration duration;
    }
}
