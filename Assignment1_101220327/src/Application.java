import common.Enums;
import controller.Assembler;
import controller.Technician;
import model.AssemblyTable;
import service.LoggerService;

import java.util.logging.Logger;

public class Application {
    private static final Logger logger = LoggerService.getLogger(false);

    public static void main(String[] args) {
        logger.info("Application starting...");

        AssemblyTable table = new AssemblyTable();

        Thread assembler = new Thread(new Assembler(table), "Assembler");

        Thread techFrame = new Thread(new Technician(table, Enums.ComponentType.FRAME), "Technician-FRAME");
        Thread techPropulsion = new Thread(new Technician(table, Enums.ComponentType.PROPULSION), "Technician-PROPULSION");
        Thread techFirmware = new Thread(new Technician(table, Enums.ComponentType.FIRMWARE), "Technician-FIRMWARE");

        assembler.start();

        techFrame.start();
        techPropulsion.start();
        techFirmware.start();

        logger.info("All threads started.");
    }
}
