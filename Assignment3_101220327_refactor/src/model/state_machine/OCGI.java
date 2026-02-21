package model.state_machine;

import common.enums.CarSignalType;
import common.enums.EventType;
import common.enums.PedSignalType;
import common.enums.StateType;
import model.interfaces.PelicanCrossingContextInterface;
import model.interfaces.StateMachineInterface;

import java.util.logging.Logger;

public class OCGI implements StateMachineInterface {
    @Override
    public StateType getState() {
        return StateType.OPERATIONAL_CARS_GREEN_INT;
    }

    @Override
    public void onInit(PelicanCrossingContextInterface pcc, Logger logger) {
        pcc.setStateType(StateType.OPERATIONAL_CARS_GREEN_INT);
        pcc.setCarSignalType(CarSignalType.GREEN);
        pcc.setPedSignalType(PedSignalType.DONT_WALK_ON);
    }

    @Override
    public void onPedsWaiting(PelicanCrossingContextInterface pcc, Logger logger) {
        pcc.transitionTo(new OCY(), EventType.Q_TIMEOUT);
    }

    @Override
    public void onOff(PelicanCrossingContextInterface pcc, Logger logger) {
        pcc.transitionTo(new FFO(), EventType.OFF);
    }
}
