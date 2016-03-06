package vitaminrx.cli;

import java.io.IOException;
import java.io.Reader;

public class Console {
    private static final int END_OF_READER = -1;
    private final KeyReceiver keyReceiver;

    public Console(KeyReceiver keyReceiver) {
        this.keyReceiver = keyReceiver;
    }

    @SuppressWarnings({"StandardVariableNames", "NestedAssignment"})
    public void read(Reader in) throws IOException {
        int ch;
        while (
            (ch = in.read()) != END_OF_READER &&
                keyReceiver.isNotTerminated()) {
            keyReceiver.keyPressed((char) ch);
        }
    }
}
