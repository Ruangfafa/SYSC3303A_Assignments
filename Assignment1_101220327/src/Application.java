import common.ConfigLoader;
import common.Enums;
import common.Constants.ApplicationCon;
import controller.Assembler;
import controller.Technician;
import model.AssemblyTable;
import service.LoggerService;

import java.util.logging.Logger;

/**
 * Main class of the application.
 * Initializes shared resources and starts all threads.
 */
public class Application {
    private static final Logger logger = LoggerService.getLogger(ConfigLoader.getInstance().ifLogOutput());

    /**
     * Starts the application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        logger.info(ApplicationCon.L_START);

        AssemblyTable table = new AssemblyTable();

        Thread assembler = new Thread(new Assembler(table), ApplicationCon.ASSEMBLER);

        Thread techFrame = new Thread(new Technician(table, Enums.ComponentType.FRAME), ApplicationCon.TECH_FRAME);
        Thread techPropulsion = new Thread(new Technician(table, Enums.ComponentType.PROPULSION), ApplicationCon.TECH_PROP);
        Thread techFirmware = new Thread(new Technician(table, Enums.ComponentType.FIRMWARE), ApplicationCon.TECH_FIRM);

        assembler.start();

        techFrame.start();
        techPropulsion.start();
        techFirmware.start();

        logger.info(ApplicationCon.L_OVER);
    }
}
