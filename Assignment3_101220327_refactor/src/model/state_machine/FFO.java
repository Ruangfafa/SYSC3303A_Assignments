package model.state_machine;

import common.enums.CarSignalType;
import common.enums.EventType;
import common.enums.PedSignalType;
import common.enums.StateType;
import model.interfaces.PelicanCrossingContextInterface;
import model.interfaces.StateMachineInterface;

import java.util.logging.Logger;

public class FFO implements StateMachineInterface {
    @Override
    public StateType getState() {
        return StateType.OFFLINE_FLASH_ON;
    }

    @Override
    public void onInit(PelicanCrossingContextInterface pcc, Logger logger) {
        pcc.setStateType(StateType.OFFLINE_FLASH_ON);
        pcc.setCarSignalType(CarSignalType.FLASHING_AMBER_ON);
        pcc.setPedSignalType(PedSignalType.DONT_WALK_ON);
    }

    @Override
    public void onQTimeout(PelicanCrossingContextInterface pcc, Logger logger) {
        pcc.transitionTo(new FFF(), EventType.Q_TIMEOUT);
    }

    @Override
    public void onOn(PelicanCrossingContextInterface pcc, Logger logger) {
        pcc.transitionTo(new OCGNP(), EventType.ON);
    }
}
