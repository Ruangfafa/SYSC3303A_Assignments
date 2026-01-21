package controller;

import common.ConfigLoader;
import common.Constants.TechnicianCon;
import model.AssemblyTable;
import common.Enums.ComponentType;
import service.LoggerService;

import java.util.logging.Logger;

public class Technician implements Runnable {
    private static final Logger logger = LoggerService.getLogger(ConfigLoader.getInstance().ifLogOutput());

    private final AssemblyTable table;
    private final ComponentType type;

    public Technician(AssemblyTable table, ComponentType type) {
        this.table = table;
        this.type = type;
    }

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
