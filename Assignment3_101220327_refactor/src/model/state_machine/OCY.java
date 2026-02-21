package model.state_machine;

import common.ConfigLoader;
import common.enums.CarSignalType;
import common.enums.EventType;
import common.enums.PedSignalType;
import common.enums.StateType;
import model.interfaces.PelicanCrossingContextInterface;
import model.interfaces.StateMachineInterface;

import java.util.logging.Logger;

public class OCY implements StateMachineInterface {
    @Override
    public StateType getState() {
        return StateType.OPERATIONAL_CARS_YELLOW;
    }

    @Override
    public void onInit(PelicanCrossingContextInterface pcc, Logger logger) {
        final int YELLOW_TOUT = ConfigLoader.getInstance().getYellowTimeOut();

        pcc.setStateType(StateType.OPERATIONAL_CARS_YELLOW);
        pcc.setCarSignalType(CarSignalType.YELLOW);
        pcc.setPedSignalType(PedSignalType.DONT_WALK_ON);
        pcc.setTicker(YELLOW_TOUT);
    }

    @Override
    public void onQTimeout(PelicanCrossingContextInterface pcc, Logger logger) {
        int ticker = pcc.getTicker();

        pcc.setTicker(--ticker);
        if (ticker <= 0) pcc.transitionTo(new OPW(), EventType.Q_TIMEOUT);
    }

    @Override
    public void onOff(PelicanCrossingContextInterface pcc, Logger logger) {
        pcc.transitionTo(new FFO(), EventType.OFF);
    }
}
