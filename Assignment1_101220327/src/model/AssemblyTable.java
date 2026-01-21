package model;

import common.ConfigLoader;
import common.Constants.AssemblyTableCon;
import common.Enums.ComponentType;
import service.LoggerService;

import java.util.logging.Logger;

/**
 * Shared assembly table used by assembler and technician threads.
 * Manages synchronization for placing components and assembling drones.
 * Uses a producer-consumer style pattern with synchronized, wait, and notifyAll (as is Box).
 * The maximum number of drones is fixed at application startup.
 */
public class AssemblyTable {
    private static final Logger logger = LoggerService.getLogger(ConfigLoader.getInstance().ifLogOutput());
    private static final int MAX_DRONES = ConfigLoader.getInstance().getMaxDrones();

    boolean hasFrame, hasPropulsion, hasFirmware;
    public int dronesBuilt;

    /**
     * Places two components on the assembly table.
     * Blocks if the table is not empty until it becomes available or the maximum number of drones has been reached.
     *
     * @param c1 first component to place
     * @param c2 second component to place
     * @return true if the components were placed, false if assembly is finished
     */
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
        logger.info(String.format(AssemblyTableCon.L_PLACED, c1, c2));

        notifyAll();
        return true;
    }

    /**
     * Assembles a drone if the required components are available.
     * Blocks until assembly is possible or the maximum number of drones is reached.
     *
     * @param technicianType component type handled by the technician
     * @return true if a drone was assembled, false if assembly is finished
     */
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
        logger.info(String.format(AssemblyTableCon.L_ASSEMBLED, Thread.currentThread().getName(), technicianType, dronesBuilt));

        notifyAll();
        return true;
    }

    /**
     * Marks a component as present on the table.
     *
     * @param c component to set
     */
    private void setComponent(ComponentType c) {
        switch (c) {
            case FRAME -> hasFrame = true;
            case PROPULSION -> hasPropulsion = true;
            case FIRMWARE -> hasFirmware = true;
        }
    }

    /**
     * Determines whether a technician can assemble a drone
     * based on the current table state.
     *
     * @param t technician's component type
     * @return true if assembly is possible, false otherwise
     */
    private boolean canAssemble(ComponentType t) {
        return switch (t) {
            case FRAME -> !hasFrame && hasPropulsion && hasFirmware;
            case PROPULSION -> hasFrame && !hasPropulsion && hasFirmware;
            case FIRMWARE -> hasFrame && hasPropulsion && !hasFirmware;
        };
    }
}
