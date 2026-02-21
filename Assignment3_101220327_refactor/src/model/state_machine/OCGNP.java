package model.state_machine;

import common.ConfigLoader;
import common.enums.CarSignalType;
import common.enums.EventType;
import common.enums.PedSignalType;
import common.enums.StateType;
import model.interfaces.PelicanCrossingContextInterface;
import model.interfaces.StateMachineInterface;

import java.util.logging.Logger;

public class OCGNP implements StateMachineInterface {
    @Override
    public StateType getState() {
        return StateType.OPERATIONAL_CARS_GREEN_NO_PED;
    }

    @Override
    public void onInit(PelicanCrossingContextInterface pcc, Logger logger) {
        final int GREEN_TOUT = ConfigLoader.getInstance().getGreenTimeOut();

        pcc.setStateType(StateType.OPERATIONAL_CARS_GREEN_NO_PED);
        pcc.setCarSignalType(CarSignalType.GREEN);
        pcc.setPedSignalType(PedSignalType.DONT_WALK_ON);
        pcc.setTicker(GREEN_TOUT);
    }

    @Override
    public void onPedsWaiting(PelicanCrossingContextInterface pcc, Logger logger) {
        pcc.transitionTo(new OCGPW(), EventType.PEDS_WAITING);
    }

    @Override
    public void onQTimeout(PelicanCrossingContextInterface pcc, Logger logger) {
        int ticker = pcc.getTicker();

        pcc.setTicker(--ticker);
        if (ticker <= 0) pcc.transitionTo(new OCGI(), EventType.Q_TIMEOUT);
    }

    @Override
    public void onOff(PelicanCrossingContextInterface pcc, Logger logger) {
        pcc.transitionTo(new FFO(), EventType.OFF);
    }
}
