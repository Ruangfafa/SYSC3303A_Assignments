import javax.swing.table.TableRowSorter;

/**
 * ============================================================
 * Pelican Crossing State Machine - State Pattern Solution
 * ============================================================
 *
 * Pelican Crossing Controller
 *
 * STUDENT STARTER CODE
 *
 * You MUST NOT:
 *  - Rename this class
 *  - Rename enums or enum values
 *  - Change method signatures
 *  - Remove required fields
 *
 * You MAY:
 *  - Add private helper methods
 *  - Add private classes (State Pattern)
 *  - Add private variables
 *
 * The TA JUnit harness depends on the exact names below.
 *
 * @author Dr. Rami Sabouni,
 * Systems and Computer Engineering,
 * Carleton University
 * @version 1.0, February 8, 2026
 */
public class PelicanCrossing {

    /* =========================================================
     * ENUMS — DO NOT MODIFY NAMES OR VALUES
     * ========================================================= */

    /** Events injected into the state machine */
    public enum Event {
        INIT,
        PEDS_WAITING,
        Q_TIMEOUT,
        OFF,
        ON
    }

    /** Leaf states used for grading and testing */
    public enum State {
        OPERATIONAL_CARS_GREEN_NO_PED,
        OPERATIONAL_CARS_GREEN_PED_WAIT,
        OPERATIONAL_CARS_GREEN_INT,
        OPERATIONAL_CARS_YELLOW,
        OPERATIONAL_PEDS_WALK,
        OPERATIONAL_PEDS_FLASH,

        OFFLINE_FLASH_ON,
        OFFLINE_FLASH_OFF
    }

    /** Output signal for cars */
    public enum CarSignal {
        RED,
        GREEN,
        YELLOW,
        FLASHING_AMBER_ON,
        FLASHING_AMBER_OFF
    }

    /** Output signal for pedestrians */
    public enum PedSignal {
        DONT_WALK_ON,
        DONT_WALK_OFF,
        WALK
    }

    /* =========================================================
     * TIMING CONSTANTS (ticks)
     * DO NOT RENAME — values may be changed if justified
     * ========================================================= */

    public static final int GREEN_TOUT  = 3;   // minimum green duration
    public static final int YELLOW_TOUT = 2;   // yellow duration
    public static final int WALK_TOUT   = 3;   // walk duration
    public static final int PED_FLASH_N = 6;   // number of flashing ticks

    /* =========================================================
     * REQUIRED INTERNAL STATE
     * ========================================================= */

    /** Current leaf state (used by TA tests) */
    private State state;

    /** Output signals (used by TA tests) */
    private CarSignal carSignal;
    private PedSignal pedSignal;

    /* =========================================================
     * CONSTRUCTOR
     * ========================================================= */

    public PelicanCrossing() {
        // Students should trigger initialization here
        // Example: dispatch(Event.INIT);
        dispatch(Event.INIT);
    }

    /* =========================================================
     * REQUIRED PUBLIC API — DO NOT CHANGE SIGNATURES
     * ========================================================= */

    /**
     * Inject an event into the state machine.
     */
    public void dispatch(Event e) {
        // TODO: student implementation
        switch (e) {
            case INIT -> current.onInit();
            case PEDS_WAITING -> current.onPedsWaiting();
            case Q_TIMEOUT -> current.onQTimeout();
            case OFF -> current.onOff();
            case ON -> current.onOn();
            case null, default -> throw new IllegalStateException("State Error");
        }
    }

    /**
     * Convenience method: advance the clock by n ticks.
     * Each tick corresponds to one Q_TIMEOUT event.
     */
    public void tick(int n) {
        // TODO: student implementation
        for (int i = n; i > 0; i--) {
            System.out.println("ticked: " + i);
            dispatch(Event.Q_TIMEOUT);
        }
    }

    /**
     * Return the current leaf state.
     * Used directly by the TA JUnit harness.
     */
    public State getState() {
        return state;
    }

    /**
     * Return the current car signal.
     */
    public CarSignal getCarSignal() {
        return carSignal;
    }

    /**
     * Return the current pedestrian signal.
     */
    public PedSignal getPedSignal() {
        return pedSignal;
    }

    /* =========================================================
     * STUDENT NOTES
     * =========================================================
     *
     * - You may implement this using:
     *     • enum + switch
     *     • State Pattern (recommended)
     * - You may add:
     *     • private helper methods
     *     • private counters/timers
     *     • private inner classes
     *
     * - You MUST ensure:
     *     • OFF works from any operational state
     *     • ON works from any offline state
     *     • getState(), getCarSignal(), getPedSignal()
     *       always reflect the CURRENT behavior
     *
     * - The TA JUnit harness will:
     *     • inject events in arbitrary order
     *     • call tick(n)
     *     • assert exact states and outputs
     */
    private interface StateMachineInterface {
        PelicanCrossing.State name();
        void onInit();
        void onPedsWaiting();
        void onQTimeout();
        void onOff();
        void onOn();
        void ignore();
    }

    private StateMachineInterface current = new OCGNP();
    private int ticker = 0;
    private int flashTicker = 0;

    private class OCGNP implements StateMachineInterface {
        @Override
        public State name() {
            return State.OPERATIONAL_CARS_GREEN_NO_PED;
        }

        @Override
        public void onInit() {
            state = State.OPERATIONAL_CARS_GREEN_NO_PED;
            carSignal = CarSignal.GREEN;
            pedSignal = PedSignal.DONT_WALK_ON;
            ticker = GREEN_TOUT;
        }

        @Override
        public void onPedsWaiting() {
            transitionTo(new OCGPW(), Event.PEDS_WAITING);
        }

        @Override
        public void onQTimeout() {
            if (--ticker <= 0) transitionTo(new OCGI(), Event.Q_TIMEOUT);
        }

        @Override
        public void onOff() {
            transitionTo(new FFO(), Event.OFF);
        }

        @Override
        public void onOn() {

        }

        @Override
        public void ignore() {

        }
    }

    private class OCGPW implements StateMachineInterface {

        @Override
        public State name() {
            return State.OPERATIONAL_CARS_GREEN_PED_WAIT;
        }

        @Override
        public void onInit() {

        }

        @Override
        public void onPedsWaiting() {

        }

        @Override
        public void onQTimeout() {
            if (--ticker <= 0) {
                transitionTo(new OCY(), Event.Q_TIMEOUT);
            }
        }

        @Override
        public void onOff() {
            transitionTo(new FFO(), Event.OFF);
        }

        @Override
        public void onOn() {

        }

        @Override
        public void ignore() {

        }
    }

    private class OCGI implements StateMachineInterface {

        @Override
        public State name() {
            return State.OPERATIONAL_CARS_GREEN_INT;
        }

        @Override
        public void onInit() {
            state = State.OPERATIONAL_CARS_GREEN_INT;
            carSignal = CarSignal.GREEN;
            pedSignal = PedSignal.DONT_WALK_ON;
        }

        @Override
        public void onPedsWaiting() {
            transitionTo(new OCY(), Event.Q_TIMEOUT);
        }

        @Override
        public void onQTimeout() {

        }

        @Override
        public void onOff() {
            transitionTo(new FFO(), Event.OFF);
        }

        @Override
        public void onOn() {

        }

        @Override
        public void ignore() {

        }
    }

    public class OCY implements StateMachineInterface {

        @Override
        public State name() {
            return State.OPERATIONAL_CARS_YELLOW;
        }

        @Override
        public void onInit() {
            state = State.OPERATIONAL_CARS_YELLOW;
            carSignal = CarSignal.YELLOW;
            pedSignal = PedSignal.DONT_WALK_ON;
            ticker = YELLOW_TOUT;
        }

        @Override
        public void onPedsWaiting() {

        }

        @Override
        public void onQTimeout() {
            if (--ticker <= 0) {
                transitionTo(new OPW(), Event.Q_TIMEOUT);
            }
        }

        @Override
        public void onOff() {
            transitionTo(new FFO(), Event.OFF);
        }

        @Override
        public void onOn() {

        }

        @Override
        public void ignore() {

        }
    }

    private class OPW implements StateMachineInterface {

        @Override
        public State name() {
            return State.OPERATIONAL_PEDS_WALK;
        }

        @Override
        public void onInit() {
            state = State.OPERATIONAL_PEDS_WALK;
            carSignal = CarSignal.RED;
            pedSignal = PedSignal.WALK;
            ticker = WALK_TOUT;
        }

        @Override
        public void onPedsWaiting() {

        }

        @Override
        public void onQTimeout() {
            if (--ticker <= 0) {
                transitionTo(new OPF(), Event.Q_TIMEOUT);
            }
        }

        @Override
        public void onOff() {
            transitionTo(new FFO(), Event.OFF);
        }

        @Override
        public void onOn() {

        }

        @Override
        public void ignore() {

        }
    }

    private class OPF implements StateMachineInterface {

        @Override
        public State name() {
            return State.OPERATIONAL_PEDS_FLASH;
        }

        @Override
        public void onInit() {
            state = State.OPERATIONAL_PEDS_FLASH;
            carSignal = CarSignal.RED;
            pedSignal = PedSignal.DONT_WALK_ON;
            ticker = PED_FLASH_N;
        }

        @Override
        public void onPedsWaiting() {

        }

        @Override
        public void onQTimeout() {
            if (--ticker <= 0) {
                transitionTo(new OCGNP(), Event.Q_TIMEOUT);
                return;
            }
            pedSignal = (pedSignal == PedSignal.DONT_WALK_ON) ? PedSignal.DONT_WALK_OFF : PedSignal.DONT_WALK_ON;
        }

        @Override
        public void onOff() {
            transitionTo(new FFO(), Event.OFF);
        }

        @Override
        public void onOn() {

        }

        @Override
        public void ignore() {

        }
    }

    private class FFO implements StateMachineInterface {

        @Override
        public State name() {
            return State.OFFLINE_FLASH_ON;
        }

        @Override
        public void onInit() {
            state = State.OFFLINE_FLASH_ON;
            carSignal = CarSignal.FLASHING_AMBER_ON;
            pedSignal = PedSignal.DONT_WALK_ON;
        }

        @Override
        public void onPedsWaiting() {

        }

        @Override
        public void onQTimeout() {
            transitionTo(new FFF(), Event.Q_TIMEOUT);
        }

        @Override
        public void onOff() {

        }

        @Override
        public void onOn() {
            transitionTo(new OCGNP(), Event.ON);
        }

        @Override
        public void ignore() {

        }
    }

    private class FFF implements StateMachineInterface {

        @Override
        public State name() {
            return State.OFFLINE_FLASH_OFF;
        }

        @Override
        public void onInit() {
            state = State.OFFLINE_FLASH_OFF;
            carSignal = CarSignal.FLASHING_AMBER_OFF;
            pedSignal = PedSignal.DONT_WALK_OFF;
        }

        @Override
        public void onPedsWaiting() {

        }

        @Override
        public void onQTimeout() {
            transitionTo(new FFO(), Event.Q_TIMEOUT);
        }

        @Override
        public void onOff() {

        }

        @Override
        public void onOn() {
            transitionTo(new OCGNP(), Event.ON);
        }

        @Override
        public void ignore() {

        }
    }
    //OPERATIONAL_CARS_GREEN_NO_PED,
    //OPERATIONAL_CARS_GREEN_PED_WAIT,
    //OPERATIONAL_CARS_GREEN_INT,
    //OPERATIONAL_CARS_YELLOW,
    //OPERATIONAL_PEDS_WALK,
    //OPERATIONAL_PEDS_FLASH,
    //OFFLINE_FLASH_ON,
    //OFFLINE_FLASH_OFF
    private void transitionTo(StateMachineInterface next, Event event) {
        System.out.println("[FSM] " + current.name() + " --(" + event + ")--> " + next.name());
        current = next;
        next.onInit();
        state = current.name();
    }
}