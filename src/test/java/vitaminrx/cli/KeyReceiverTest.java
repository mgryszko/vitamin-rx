package vitaminrx.cli;

import org.junit.Test;
import vitaminrx.core.TimeSlice;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static vitaminrx.cli.KeyReceiver.StateName.*;

public class KeyReceiverTest {
    @Test
    public void initial_state_is_receiving_keys() {
        assertThat(keyReceiver.state(), equalTo(RECEIVING_KEYS));
        verifyZeroInteractions(timeSlice);
    }

    @Test
    public void pause_resume_key_toggles_time_slice() {
        keyReceiver.keyPressed(SPACE);

        assertThat(keyReceiver.state(), equalTo(RECEIVING_KEYS));
        verify(timeSlice).toggle();
    }

    @Test
    public void any_other_key_pressed_does_nothing() {
        keyReceiver.keyPressed(ANY_KEY);

        assertThat(keyReceiver.state(), equalTo(RECEIVING_KEYS));
        verifyZeroInteractions(timeSlice);
    }

    @Test
    public void terminate_key_must_be_pressed_twice() {
        keyReceiver.keyPressed(NEW_LINE);

        assertThat(keyReceiver.state(), equalTo(TERMINATING));

        keyReceiver.keyPressed(NEW_LINE);

        assertThat(keyReceiver.state(), equalTo(TERMINATED));
        verifyZeroInteractions(timeSlice);
    }
    @Test
    public void after_termination_no_state_changes_nor_actions() {
        keyReceiver.keyPressed(NEW_LINE);
        keyReceiver.keyPressed(NEW_LINE);
        keyReceiver.keyPressed(SPACE);

        assertThat(keyReceiver.state(), equalTo(TERMINATED));
        verifyZeroInteractions(timeSlice);
    }

    @Test
    public void any_key_in_terminating_state_goes_back_to_receiving_keys_state() {
        keyReceiver.keyPressed(NEW_LINE);
        keyReceiver.keyPressed(ANY_KEY);

        assertThat(keyReceiver.state(), equalTo(RECEIVING_KEYS));
        verifyZeroInteractions(timeSlice);
    }

    @Test
    public void pause_resume_in_terminating_state_goes_back_to_receiving_keys_state() {
        keyReceiver.keyPressed(NEW_LINE);
        keyReceiver.keyPressed(SPACE);

        assertThat(keyReceiver.state(), equalTo(RECEIVING_KEYS));
        verify(timeSlice).toggle();
    }

    private TimeSlice timeSlice = mock(TimeSlice.class);
    private KeyReceiver keyReceiver = new KeyReceiver(timeSlice);

    private static final char SPACE = ' ';
    private static final char NEW_LINE = '\n';
    private static final char ANY_KEY = 'a';
}