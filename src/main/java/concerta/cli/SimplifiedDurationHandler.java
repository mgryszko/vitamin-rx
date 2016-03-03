package concerta.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.Messages;
import org.kohsuke.args4j.spi.OneArgumentOptionHandler;
import org.kohsuke.args4j.spi.Setter;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;

public class SimplifiedDurationHandler extends OneArgumentOptionHandler<Duration> {
    private static final Pattern PATTERN = Pattern.compile("([0-9]+)([ms])?", Pattern.CASE_INSENSITIVE);

    public SimplifiedDurationHandler(CmdLineParser parser, OptionDef option, Setter<? super Duration> setter) {
        super(parser, option, setter);
    }

    @Override
    protected Duration parse(String argument) throws CmdLineException {
        Matcher matcher = PATTERN.matcher(argument);
        if (!matcher.matches()) {
            throw misformattedArgumentException(argument);
        }
        return Duration.of(Integer.valueOf(amount(matcher)), unit(matcher));
    }

    private CmdLineException misformattedArgumentException(String token) {
        return new CmdLineException(owner, Messages.ILLEGAL_OPERAND, option.toString(), token);
    }

    private String amount(MatchResult result) {
        return result.group(1);
    }

    private TemporalUnit unit(MatchResult result) {
        return "s".equalsIgnoreCase(result.group(2)) ? SECONDS : MINUTES;
    }

    @Override
    public String getDefaultMetaVariable() {
        return "N|Nm|Ns";
    }
}
