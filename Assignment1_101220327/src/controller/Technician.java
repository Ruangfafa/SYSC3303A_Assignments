package controller;

import model.AssemblyTable;
import common.Enums.ComponentType;
import service.LoggerService;

import java.util.logging.Logger;

public class Technician implements Runnable {
    private static final Logger logger = LoggerService.getLogger(false);

    private final AssemblyTable table;
    private final ComponentType type;

    public Technician(AssemblyTable table, ComponentType type) {
        this.table = table;
        this.type = type;
    }

    @Override
    public void run() {
        logger.info("Technician started: " + type);
        while (!Thread.currentThread().isInterrupted()) {
            if (!table.assemble(type)) {
                logger.info("[" + Thread.currentThread().getName() + "] Technician exiting.");
                return;
            }
        }
    }
}
