package model.state_machine;

import common.enums.CarSignalType;
import common.enums.EventType;
import common.enums.PedSignalType;
import common.enums.StateType;
import model.interfaces.PelicanCrossingContextInterface;
import model.interfaces.StateMachineInterface;

import java.util.logging.Logger;

public class OCGPW implements StateMachineInterface {
    @Override
    public StateType getState() {
        return StateType.OPERATIONAL_CARS_GREEN_PED_WAIT;
    }

    @Override
    public void onInit(PelicanCrossingContextInterface pcc, Logger logger) {
        pcc.setStateType(StateType.OPERATIONAL_CARS_GREEN_PED_WAIT);
        pcc.setCarSignalType(CarSignalType.GREEN);
        pcc.setPedSignalType(PedSignalType.DONT_WALK_ON);
    }

    @Override
    public void onQTimeout(PelicanCrossingContextInterface pcc, Logger logger) {
        int ticker = pcc.getTicker();

        pcc.setTicker(--ticker);
        if (ticker <= 0) pcc.transitionTo(new OCY(), EventType.Q_TIMEOUT);
    }

    @Override
    public void onOff(PelicanCrossingContextInterface pcc, Logger logger) {
        pcc.transitionTo(new FFO(), EventType.OFF);
    }
}
