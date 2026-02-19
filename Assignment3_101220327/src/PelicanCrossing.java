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
    }

    /* =========================================================
     * REQUIRED PUBLIC API — DO NOT CHANGE SIGNATURES
     * ========================================================= */

    /**
     * Inject an event into the state machine.
     */
    public void dispatch(Event e) {
        // TODO: student implementation
    }

    /**
     * Convenience method: advance the clock by n ticks.
     * Each tick corresponds to one Q_TIMEOUT event.
     */
    public void tick(int n) {
        // TODO: student implementation
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
}