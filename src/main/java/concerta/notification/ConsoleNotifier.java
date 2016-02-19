package concerta.notification;

import concerta.core.Event;
import org.fusesource.jansi.Ansi;
import rx.functions.Action1;

import java.io.PrintStream;

import static org.fusesource.jansi.Ansi.ansi;

public class ConsoleNotifier implements Action1<Event> {
    private final EventMessageFormatter formatter;
    private final PrintStream outStream;

    public ConsoleNotifier(EventMessageFormatter formatter, PrintStream outStream) {
        this.formatter = formatter;
        this.outStream = outStream;
    }

    @Override
    public void call(Event event) {
        deleteCurrentLine();
        outStream.println(formatter.message(event));
    }

    private void deleteCurrentLine() {
        outStream.print(ansi().cursorUp(1).eraseLine(Ansi.Erase.ALL));
        outStream.flush();
    }
}
