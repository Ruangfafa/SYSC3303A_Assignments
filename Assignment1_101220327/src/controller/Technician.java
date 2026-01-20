package controller;

import common.ConfigLoader;
import common.Constants.TechnicianCon;
import model.AssemblyTable;
import common.Enums.ComponentType;
import service.LoggerService;

import java.util.logging.Logger;

/**
 * Consumer thread responsible for assembling drones.
 *
 * Each technician handles one specific component type and attempts to assemble a drone when the other required components are available on the assembly table.
 */
public class Technician implements Runnable {
    private static final Logger logger = LoggerService.getLogger(ConfigLoader.getInstance().ifLogOutput());

    private final AssemblyTable table;
    private final ComponentType type;

    /**
     * Creates a new technician.
     *
     * @param table shared assembly table
     * @param type  component type handled by this technician
     */
    public Technician(AssemblyTable table, ComponentType type) {
        this.table = table;
        this.type = type;
    }

    /**
     * Main execution loop of the assembler thread.
     * Continuously places random component pairs on the table until assembly is complete or the thread is interrupted.
     */
    @Override
    public void run() {
        logger.info(String.format(TechnicianCon.L_START, type));
        while (!Thread.currentThread().isInterrupted()) {
            if (!table.assemble(type)) {
                logger.info(String.format(TechnicianCon.L_EXIT, Thread.currentThread().getName()));
                return;
            }
        }
    }
}
