package vitaminrx.cli;

import vitaminrx.core.TimeSlice;

import static vitaminrx.cli.KeyReceiver.StateName.*;

class KeyReceiver {
    private static final char PAUSE_RESUME = ' ';
    private static final char TERMINATE = '\n';
    private TimeSlice timeSlice;
    private State receivingKeys = new ReceivingKeys();
    private State terminating = new Terminating();
    private State terminated = new Terminated();
    private State state = receivingKeys;

    public KeyReceiver(TimeSlice timeSlice) {
        this.timeSlice = timeSlice;
    }

    public void keyPressed(char key) {
        state = state.keyPressed(key);
    }

    public StateName state() {
        return state.name();
    }

    public boolean isNotTerminated() {
        return state.name() != TERMINATED;
    }

    public enum StateName {
        RECEIVING_KEYS, TERMINATING, TERMINATED;
    }

    private interface State {
        State keyPressed(char key);

        StateName name();
    }

    private class ReceivingKeys implements State {
        @Override
        public State keyPressed(char key) {
            State nextState = this;
            if (key == PAUSE_RESUME) {
                timeSlice.toggle();
            } else if (key == TERMINATE) {
                nextState = terminating;
            }
            return nextState;
        }

        @Override
        public StateName name() {
            return RECEIVING_KEYS;
        }
    }

    private class Terminating implements State {
        @Override
        public State keyPressed(char key) {
            if (key == TERMINATE) {
                return terminated;
            }
            if (key == PAUSE_RESUME) {
                timeSlice.toggle();
                return receivingKeys;
            }
            return receivingKeys;
        }

        @Override
        public StateName name() {
            return TERMINATING;
        }
    }

    private class Terminated implements State {
        @Override
        public State keyPressed(char key) {
            return terminated;
        }

        @Override
        public StateName name() {
            return TERMINATED;
        }
    }
}
