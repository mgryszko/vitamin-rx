package concerta.notification;

import concerta.core.Event;
import rx.functions.Action1;

import java.io.IOException;
import java.io.PrintStream;

public class GrowlNotifier implements Action1<Event> {
    private final EventFormatter formatter;
    private final PrintStream errorStream;

    public GrowlNotifier(EventFormatter formatter, PrintStream errorStream) {
        this.formatter = formatter;
        this.errorStream = errorStream;
    }

    @Override
    public void call(Event event) {
        try {
            new ProcessBuilder("growlnotify", "Time slice", "-m", event.format(formatter)).start();
        } catch (IOException e) {
            errorStream.println(e.getMessage());
        }
    }
}
