package controller;

import common.enums.EventType;
import model.PelicanCrossing;
import service.LoggerService;

import java.util.logging.Logger;

public class PelicanCrossingController {
    private static final Logger logger = LoggerService.getLogger();

    private final PelicanCrossing pelicanCrossing;

    public PelicanCrossingController(PelicanCrossing pelicanCrossing) {
        this.pelicanCrossing = pelicanCrossing;
    }

    public void tick(int n) {
        for (int i = n; i > 0; i--) {
            logger.info("tick:" + i);
            pelicanCrossing.dispatch(EventType.Q_TIMEOUT);
        }
    }

    public void pedsWaitingClick() {
        pelicanCrossing.dispatch(EventType.PEDS_WAITING);
    }

    public void on() { pelicanCrossing.dispatch(EventType.ON); }
    public void off() { pelicanCrossing.dispatch(EventType.OFF); }
}
