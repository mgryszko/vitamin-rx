package vitaminrx.cli;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.DelimitedOptionHandler;
import org.kohsuke.args4j.spi.Setter;

import java.time.Duration;

public class MultiDurationOptionHandler extends DelimitedOptionHandler<Duration> {
    public MultiDurationOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super Duration> setter) {
        super(parser, option, setter, ",", new SimplifiedDurationHandler(parser, option, setter));
    }
}
