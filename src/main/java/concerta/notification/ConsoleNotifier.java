package concerta.notification;

import concerta.core.Event;
import rx.functions.Action1;

public class ConsoleNotifier implements Action1<Event> {
    private EventMessageFormatter formatter;

    public ConsoleNotifier(EventMessageFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void call(Event event) {
        System.out.println(formatter.message(event));
    }
}
