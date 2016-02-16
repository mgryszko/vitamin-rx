package concerta.notification;

import concerta.core.Event;
import rx.functions.Action1;

import java.io.PrintStream;

public class ConsoleNotifier implements Action1<Event> {
    private final EventMessageFormatter formatter;
    private final PrintStream outStream;

    public ConsoleNotifier(EventMessageFormatter formatter, PrintStream outStream) {
        this.formatter = formatter;
        this.outStream = outStream;
    }

    @Override
    public void call(Event event) {
        outStream.println(formatter.message(event));
    }
}
