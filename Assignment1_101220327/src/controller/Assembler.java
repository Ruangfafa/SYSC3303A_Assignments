package controller;

import common.ConfigLoader;
import model.AssemblyTable;
import common.Enums.ComponentType;
import common.Constants.AssemblerCon;
import service.LoggerService;

import java.util.Random;
import java.util.logging.Logger;

public class Assembler implements Runnable {
    private static final Logger logger = LoggerService.getLogger(ConfigLoader.getInstance().ifLogOutput());

    private final AssemblyTable table;
    private final Random random = new Random();

    public Assembler(AssemblyTable table) {
        this.table = table;
    }

    @Override
    public void run() {
        logger.info(AssemblerCon.L_START);
        while (!Thread.currentThread().isInterrupted()) {
            ComponentType[] pair = pickTwoComponents();
            if (!table.placeComponents(pair[0], pair[1])) {
                logger.info(AssemblerCon.L_EXIT);
                return;
            }
        }
    }

    private ComponentType[] pickTwoComponents() {
        ComponentType[] values = ComponentType.values();
        int i = random.nextInt(values.length);
        int j;
        do {
            j = random.nextInt(values.length);
        } while (j == i);

        return new ComponentType[]{values[i], values[j]};
    }
}
