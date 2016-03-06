package vitaminrx.cli;

import org.junit.Test;

import java.io.StringReader;

import static org.mockito.Mockito.*;

public class ConsoleTest {
    @Test
    public void passes_read_characters_to_receiver() throws Exception {
        StringReader in = new StringReader(" ");
        when(keyReceiver.isNotTerminated()).thenReturn(true);

        controller.read(in);

        verify(keyReceiver).keyPressed(space);
    }

    @Test
    public void terminates_reading_when_receiver_has_terminated() throws Exception {
        StringReader in = new StringReader("  ");
        when(keyReceiver.isNotTerminated())
            .thenReturn(true)
            .thenReturn(false);

        controller.read(in);
        verify(keyReceiver).keyPressed(space);
    }

    private KeyReceiver keyReceiver = mock(KeyReceiver.class);
    private Console controller = new Console(keyReceiver);
    private final char space = ' ';
}
