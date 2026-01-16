package model;

import common.Enums.ComponentType;
import service.LoggerService;

import java.util.logging.Logger;

public class AssemblyTable {
    private static final Logger logger = LoggerService.getLogger(false);
    private static final int MAX_DRONES = 20;

    boolean hasFrame, hasPropulsion, hasFirmware;
    public int dronesBuilt;

    public synchronized boolean placeComponents(ComponentType c1, ComponentType c2) {
        while ((hasFrame || hasPropulsion || hasFirmware) && dronesBuilt < MAX_DRONES) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        if (dronesBuilt >= MAX_DRONES) {
            notifyAll();
            return false;
        }

        setComponent(c1);
        setComponent(c2);
        logger.info("Components placed on table: " + c1 + ", " + c2);

        notifyAll();
        return true;
    }

    public synchronized boolean assemble(ComponentType technicianType) {
        while (!canAssemble(technicianType) && dronesBuilt < MAX_DRONES) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        if (dronesBuilt >= MAX_DRONES) {
            notifyAll();
            return false;
        }

        hasFrame = false;
        hasPropulsion = false;
        hasFirmware = false;

        dronesBuilt++;
        logger.info("[" + Thread.currentThread().getName() + "] " + "Drone assembled by " + technicianType + " | Total = " + dronesBuilt);

        notifyAll();
        return true;
    }

    private void setComponent(ComponentType c) {
        switch (c) {
            case FRAME -> hasFrame = true;
            case PROPULSION -> hasPropulsion = true;
            case FIRMWARE -> hasFirmware = true;
        }
    }

    private boolean canAssemble(ComponentType t) {
        return switch (t) {
            case FRAME -> !hasFrame && hasPropulsion && hasFirmware;
            case PROPULSION -> hasFrame && !hasPropulsion && hasFirmware;
            case FIRMWARE -> hasFrame && hasPropulsion && !hasFirmware;
        };
    }
}
