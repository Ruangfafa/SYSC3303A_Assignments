package model.state_machine;

import common.ConfigLoader;
import common.enums.CarSignalType;
import common.enums.EventType;
import common.enums.PedSignalType;
import common.enums.StateType;
import model.interfaces.PelicanCrossingContextInterface;
import model.interfaces.StateMachineInterface;

import java.util.logging.Logger;

public class OPW implements StateMachineInterface {
    @Override
    public StateType getState() {
        return StateType.OPERATIONAL_PEDS_WALK;
    }

    @Override
    public void onInit(PelicanCrossingContextInterface pcc, Logger logger) {
        final int WALK_TOUT = ConfigLoader.getInstance().getWalkTimeOut();

        pcc.setStateType(StateType.OPERATIONAL_PEDS_WALK);
        pcc.setCarSignalType(CarSignalType.RED);
        pcc.setPedSignalType(PedSignalType.WALK);
        pcc.setTicker(WALK_TOUT);
    }

    @Override
    public void onQTimeout(PelicanCrossingContextInterface pcc, Logger logger) {
        int ticker = pcc.getTicker();

        pcc.setTicker(--ticker);
        if (ticker <= 0) pcc.transitionTo(new OPF(), EventType.Q_TIMEOUT);
    }

    @Override
    public void onOff(PelicanCrossingContextInterface pcc, Logger logger) {
        pcc.transitionTo(new FFO(), EventType.OFF);
    }
}
