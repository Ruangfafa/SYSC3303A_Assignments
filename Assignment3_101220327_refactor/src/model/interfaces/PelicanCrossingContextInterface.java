package model.interfaces;

import common.enums.CarSignalType;
import common.enums.EventType;
import common.enums.PedSignalType;
import common.enums.StateType;

import java.util.logging.Logger;

public interface PelicanCrossingContextInterface {
    StateType getStateType();
    void setStateType(StateType stateType);

    CarSignalType getCarSignalType();
    void setCarSignalType(CarSignalType carSignalType);

    PedSignalType getPedSignalType();
    void setPedSignalType(PedSignalType pedSignalType);

    int getTicker();
    void setTicker(int ticker);

    void transitionTo(StateMachineInterface stateMachineInterface, EventType eventType);
}
