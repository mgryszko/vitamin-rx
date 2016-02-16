package concerta.notification;

import concerta.core.Event;
import rx.functions.Action1;

import java.io.IOException;
import java.io.PrintStream;

public class GrowlNotifier implements Action1<Event> {
    private final EventMessageFormatter formatter;
    private final PrintStream errorStream;

    public GrowlNotifier(EventMessageFormatter formatter, PrintStream errorStream) {
        this.formatter = formatter;
        this.errorStream = errorStream;
    }

    @Override
    public void call(Event event) {
        try {
            new ProcessBuilder("growlnotify", "-m", formatter.message(event)).start();
        } catch (IOException e) {
            errorStream.println(e.getMessage());
        }
    }
}
