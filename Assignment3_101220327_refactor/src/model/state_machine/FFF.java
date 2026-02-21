package model.state_machine;

import common.enums.CarSignalType;
import common.enums.EventType;
import common.enums.PedSignalType;
import common.enums.StateType;
import model.interfaces.PelicanCrossingContextInterface;
import model.interfaces.StateMachineInterface;

import java.util.logging.Logger;

public class FFF implements StateMachineInterface {
    @Override
    public StateType getState() {
        return StateType.OFFLINE_FLASH_OFF;
    }

    @Override
    public void onInit(PelicanCrossingContextInterface pcc, Logger logger) {
        pcc.setStateType(StateType.OFFLINE_FLASH_OFF);
        pcc.setCarSignalType(CarSignalType.FLASHING_AMBER_OFF);
        pcc.setPedSignalType(PedSignalType.DONT_WALK_OFF);
    }

    @Override
    public void onQTimeout(PelicanCrossingContextInterface pcc, Logger logger) {
        pcc.transitionTo(new FFO(), EventType.Q_TIMEOUT);
    }

    @Override
    public void onOn(PelicanCrossingContextInterface pcc, Logger logger) {
        pcc.transitionTo(new OCGNP(), EventType.ON);
    }
}
