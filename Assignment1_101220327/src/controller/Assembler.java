package controller;

import model.AssemblyTable;
import common.Enums.ComponentType;
import service.LoggerService;

import java.util.Random;
import java.util.logging.Logger;

public class Assembler implements Runnable {
    private static final Logger logger = LoggerService.getLogger(false);

    private final AssemblyTable table;
    private final Random random = new Random();

    public Assembler(AssemblyTable table) {
        this.table = table;
    }

    @Override
    public void run() {
        logger.info("Assembler started.");
        while (!Thread.currentThread().isInterrupted()) {
            ComponentType[] pair = pickTwoComponents();
            logger.info("Assembler placing components: " + pair[0] + ", " + pair[1]);

            if (!table.placeComponents(pair[0], pair[1])) {
                logger.info("Assembly complete. Assembler exiting.");
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
