package model.interfaces;

import common.enums.StateType;

import java.util.logging.Logger;

public interface StateMachineInterface {
    StateType getState();

    void onInit(PelicanCrossingContextInterface pcc, Logger logger);

    default void onPedsWaiting(PelicanCrossingContextInterface pcc, Logger logger){
        ignored(logger);
    }

    default void onQTimeout(PelicanCrossingContextInterface pcc, Logger logger){
        ignored(logger);
    }

    default void onOff(PelicanCrossingContextInterface pcc, Logger logger){
        ignored(logger);
    }

    default void onOn(PelicanCrossingContextInterface pcc, Logger logger){
        ignored(logger);
    }

    default void ignored(Logger logger){
        logger.warning("State Machine Call Ignored");
    }
}
