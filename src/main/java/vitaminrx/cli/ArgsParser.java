package vitaminrx.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;

import java.io.PrintStream;

class ArgsParser {
    private final CmdLineParser parser;
    private final PrintStream errorStream;

    public ArgsParser(Object target, PrintStream errorStream) {
        this.errorStream = errorStream;
        parser = createCmdLineParser(target);
    }

    @SuppressWarnings("CallToSystemExit")
    void parse(String... args) {
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            printErrorAndUsage(e);
            System.exit(1);
        }
    }

    private CmdLineParser createCmdLineParser(Object target) {
        ParserProperties parserProperties = ParserProperties.defaults()
            .withShowDefaults(false)
            .withOptionValueDelimiter("=");
        return new CmdLineParser(target, parserProperties);
    }

    private void printErrorAndUsage(CmdLineException e) {
        errorStream.println(e.getMessage());
        errorStream.println();
        parser.printUsage(errorStream);
    }
}
