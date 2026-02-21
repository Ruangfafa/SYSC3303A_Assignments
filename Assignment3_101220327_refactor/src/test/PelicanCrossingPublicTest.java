package test;

import common.ConfigLoader;
import common.enums.CarSignalType;
import common.enums.EventType;
import common.enums.PedSignalType;
import common.enums.StateType;
import controller.PelicanCrossingController;
import model.PelicanCrossing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PelicanCrossingPublicTest {
    static final int GREEN_TOUT = ConfigLoader.getInstance().getGreenTimeOut();
    static final int YELLOW_TOUT = ConfigLoader.getInstance().getYellowTimeOut();
    static final int WALK_TOUT = ConfigLoader.getInstance().getWalkTimeOut();
    static final int PED_FLASH_N = ConfigLoader.getInstance().getPedFlashN();

    @Test
    void init_entersOperationalCarsGreenNoPed() {
        PelicanCrossing fsm = new PelicanCrossing();
        Assertions.assertEquals(StateType.OPERATIONAL_CARS_GREEN_NO_PED, fsm.getStateType());
        Assertions.assertEquals(CarSignalType.GREEN, fsm.getCarSignalType());
        Assertions.assertEquals(PedSignalType.DONT_WALK_ON, fsm.getPedSignalType());
    }

    @Test
    void pedsWaiting_beforeMinGreen_remembersWaitingButStaysInCarsGreen() {
        PelicanCrossing fsm = new PelicanCrossing();
        PelicanCrossingController fsmc = new PelicanCrossingController(fsm);
        fsmc.pedsWaitingClick();
        Assertions.assertEquals(StateType.OPERATIONAL_CARS_GREEN_PED_WAIT, fsm.getStateType());
        Assertions.assertEquals(CarSignalType.GREEN, fsm.getCarSignalType());
        Assertions.assertEquals(PedSignalType.DONT_WALK_ON, fsm.getPedSignalType());
    }

    @Test
    void pedWaiting_duringMinGreen_thenAutoYellow_whenMinGreenEnds() {
        PelicanCrossing fsm = new PelicanCrossing();
        PelicanCrossingController fsmc = new PelicanCrossingController(fsm);
        fsm.dispatch(EventType.PEDS_WAITING);
        Assertions.assertEquals(StateType.OPERATIONAL_CARS_GREEN_PED_WAIT, fsm.getStateType());
        fsmc.tick(GREEN_TOUT);
        Assertions.assertEquals(StateType.OPERATIONAL_CARS_YELLOW, fsm.getStateType());
        Assertions.assertEquals(CarSignalType.YELLOW, fsm.getCarSignalType());
    }

    @Test
    void walkTimesOut_thenFlash() {
        PelicanCrossing fsm = new PelicanCrossing();
        PelicanCrossingController fsmc = new PelicanCrossingController(fsm);
        fsmc.tick(GREEN_TOUT);
        fsmc.pedsWaitingClick();
        fsmc.tick(YELLOW_TOUT);
        fsmc.tick(WALK_TOUT);
        Assertions.assertEquals(StateType.OPERATIONAL_PEDS_FLASH, fsm.getStateType());
        Assertions.assertEquals(CarSignalType.RED, fsm.getCarSignalType());
        Assertions.assertTrue(fsm.getPedSignalType() == PedSignalType.DONT_WALK_ON || fsm.getPedSignalType() == PedSignalType.DONT_WALK_OFF);
        fsmc.tick(PED_FLASH_N);
    }

    @Test
    void offlineMode_offFromAnyOperationalState_setsSafeAndFlashes() {
        PelicanCrossing fsm = new PelicanCrossing();
        PelicanCrossingController fsmc = new PelicanCrossingController(fsm);
        fsmc.pedsWaitingClick();
        Assertions.assertTrue(fsm.getStateType().name().startsWith("OPERATIONAL_"));
        fsmc.off();
        Assertions.assertTrue(fsm.getStateType().name().startsWith("OFFLINE_"));
        Assertions.assertTrue(fsm.getCarSignalType() == CarSignalType.FLASHING_AMBER_ON || fsm.getCarSignalType() == CarSignalType.FLASHING_AMBER_OFF);
        Assertions.assertTrue(fsm.getPedSignalType() == PedSignalType.DONT_WALK_ON || fsm.getPedSignalType() == PedSignalType.DONT_WALK_OFF);
        StateType s1 = fsm.getStateType();
        fsm.dispatch(EventType.Q_TIMEOUT);
        StateType s2 = fsm.getStateType();
        Assertions.assertNotEquals(s1, s2, "Offline should toggle between flash states on each tick");
    }
}