package concerta.notification;

import concerta.core.Event;
import org.fusesource.jansi.Ansi;
import rx.functions.Action1;

import java.io.PrintStream;

import static org.fusesource.jansi.Ansi.ansi;

public class ConsoleNotifier implements Action1<Event> {
    private final EventMessageFormatter formatter;
    private final PrintStream outStream;

    private Event lastTickEvent = Event.NULL;
    private Event lastMilestoneEvent = Event.NULL;

    public ConsoleNotifier(EventMessageFormatter formatter, PrintStream outStream) {
        this.formatter = formatter;
        this.outStream = outStream;
    }

    @Override
    public void call(Event event) {
        classifyEvent(event);
        deleteCurrentLine();
        printLine();
    }

    private void classifyEvent(Event event) {
        if (event.isTick()) {
            lastTickEvent = event;
        } else {
            lastMilestoneEvent = event;
        }
    }

    private void deleteCurrentLine() {
        outStream.print(ansi().cursorUp(1).eraseLine(Ansi.Erase.ALL));
        outStream.flush();
    }

    private void printLine() {
        String line = formatter.message(lastTickEvent) + " " + formatter.message(lastMilestoneEvent);
        outStream.println(line.trim());
    }
}
