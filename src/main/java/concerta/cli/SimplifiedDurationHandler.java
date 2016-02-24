package concerta.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.Messages;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;

public class SimplifiedDurationHandler extends OptionHandler<Duration> {
    private static final Pattern PATTERN = Pattern.compile("([0-9])+([ms])?", Pattern.CASE_INSENSITIVE);
    private static final int ONE_PARSED = 1;

    public SimplifiedDurationHandler(CmdLineParser parser, OptionDef option, Setter<? super Duration> setter) {
        super(parser, option, setter);
    }

    @Override
    public int parseArguments(Parameters params) throws CmdLineException {
        String token = params.getParameter(0);
        Matcher matcher = parse(token);
        setOption(matcher);
        return ONE_PARSED;
    }

    private Matcher parse(String token) throws CmdLineException {
        Matcher matcher = PATTERN.matcher(token);
        if (!matcher.matches()) {
            throw misformattedArgumentException(token);
        }
        return matcher;
    }

    private CmdLineException misformattedArgumentException(String token) {
        return new CmdLineException(owner, Messages.ILLEGAL_OPERAND, option.toString(), token);
    }

    private void setOption(MatchResult result) throws CmdLineException {
        setter.addValue(Duration.of(Integer.valueOf(amount(result)), unit(result)));
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
