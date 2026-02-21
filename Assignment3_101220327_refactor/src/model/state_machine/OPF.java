package model.state_machine;

import common.ConfigLoader;
import common.enums.CarSignalType;
import common.enums.EventType;
import common.enums.PedSignalType;
import common.enums.StateType;
import model.interfaces.PelicanCrossingContextInterface;
import model.interfaces.StateMachineInterface;

import java.util.logging.Logger;

public class OPF implements StateMachineInterface {
    @Override
    public StateType getState() {
        return StateType.OPERATIONAL_PEDS_FLASH;
    }

    @Override
    public void onInit(PelicanCrossingContextInterface pcc, Logger logger) {
        final int PED_FLASH_N = ConfigLoader.getInstance().getPedFlashN();

        pcc.setStateType(StateType.OPERATIONAL_PEDS_FLASH);
        pcc.setCarSignalType(CarSignalType.RED);
        pcc.setPedSignalType(PedSignalType.DONT_WALK_ON);
        pcc.setTicker(PED_FLASH_N);
    }

    @Override
    public void onQTimeout(PelicanCrossingContextInterface pcc, Logger logger) {
        int ticker = pcc.getTicker();

        pcc.setTicker(--ticker);
        if (ticker <= 0) {
            pcc.transitionTo(new OCGNP(), EventType.Q_TIMEOUT);
            return;
        }
        pcc.setPedSignalType(pcc.getPedSignalType() == PedSignalType.DONT_WALK_ON ? PedSignalType.DONT_WALK_OFF : PedSignalType.DONT_WALK_ON);
    }

    @Override
    public void onOff(PelicanCrossingContextInterface pcc, Logger logger) {
        pcc.transitionTo(new FFO(), EventType.OFF);
    }
}
