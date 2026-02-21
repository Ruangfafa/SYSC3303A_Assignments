package model;

import common.enums.CarSignalType;
import common.enums.EventType;
import common.enums.PedSignalType;
import common.enums.StateType;
import model.interfaces.PelicanCrossingContextInterface;
import model.interfaces.StateMachineInterface;
import model.state_machine.OCGNP;
import service.LoggerService;

import java.util.logging.Logger;

public class PelicanCrossing implements PelicanCrossingContextInterface {
    private static final Logger logger = LoggerService.getLogger();

    private StateMachineInterface curStateMachine;
    private StateType curState;
    private CarSignalType curCarSignal;
    private PedSignalType curPedSignal;
    private int ticker;

    public PelicanCrossing() {
        curStateMachine = new OCGNP();
        dispatch(EventType.INIT);
    }

    public void dispatch(EventType e) {
        switch (e) {
            case INIT -> curStateMachine.onInit(this, logger);
            case PEDS_WAITING -> curStateMachine.onPedsWaiting(this, logger);
            case Q_TIMEOUT -> curStateMachine.onQTimeout(this, logger);
            case OFF -> curStateMachine.onOff(this, logger);
            case ON -> curStateMachine.onOn(this, logger);
            case null, default -> throw new IllegalStateException("State Error");
        }
    }

    @Override
    public StateType getStateType() {
        return curState;
    }

    @Override
    public void setStateType(StateType newState) {
        curState = newState;
    }

    @Override
    public CarSignalType getCarSignalType() {
        return curCarSignal;
    }

    @Override
    public void setCarSignalType(CarSignalType newCarSignal) {
        curCarSignal = newCarSignal;
    }

    @Override
    public PedSignalType getPedSignalType() {
        return curPedSignal;
    }

    @Override
    public void setPedSignalType(PedSignalType pedSignalType) {
        curPedSignal = pedSignalType;
    }

    @Override
    public int getTicker() {
        return ticker;
    }

    @Override
    public void setTicker(int ticker) {
        this.ticker = ticker;
    }

    @Override
    public void transitionTo(StateMachineInterface newStateMachine, EventType eventType) {
        logger.info("State transitioning: " + curStateMachine.getState() + " --(" + eventType + ")--> " + newStateMachine.getState());
        curStateMachine = newStateMachine;
        newStateMachine.onInit(this, logger);
        logger.info("Current State: " + curStateMachine.getState());
    }
}
