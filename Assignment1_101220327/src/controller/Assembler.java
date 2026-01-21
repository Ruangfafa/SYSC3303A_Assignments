package controller;

import common.ConfigLoader;
import model.AssemblyTable;
import common.Enums.ComponentType;
import common.Constants.AssemblerCon;
import service.LoggerService;

import java.util.Random;
import java.util.logging.Logger;

/**
 * Producer thread responsible for placing components on the assembly table.
 *
 * The assembler repeatedly selects two different components at random and places them on the shared AssemblyTable until assembly is finished or the thread is interrupted.
 */
public class Assembler implements Runnable {
    private static final Logger logger = LoggerService.getLogger(ConfigLoader.getInstance().ifLogOutput());

    private final AssemblyTable table;
    private final Random random = new Random();

    /**
     * Creates a new assembler.
     *
     * @param table shared assembly table
     */
    public Assembler(AssemblyTable table) {
        this.table = table;
    }

    /**
     * Main execution loop of the assembler thread.
     * Continuously places random component pairs on the table until assembly is complete or the thread is interrupted.
     */
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

    /**
     * Randomly selects two different component types.
     *
     * @return an array containing two distinct component types
     */
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
