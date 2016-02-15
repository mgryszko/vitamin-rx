package concerta.notification;

import concerta.core.Event;
import rx.functions.Action1;

import java.io.IOException;

public class GrowlNotifier implements Action1<Event> {
    private EventMessageFormatter formatter;

    public GrowlNotifier(EventMessageFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void call(Event event) {
        try {
            new ProcessBuilder("growlnotify", "-m", formatter.message(event)).start();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
