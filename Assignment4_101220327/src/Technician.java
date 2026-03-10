/**
 * This class is for the Technicians in this drone making system.
 * The Technician has an infinite supply of one of the three components.
 * The Technician will wait at the table until the other two components are placed, and will then make a drone and assemble it.
 * The Technician will repeat this procedure until 20 drones are assembled in total between all Technicians
 *
 * @author Dr. Rami Sabouni,
 * Systems and Computer Engineering,
 * Carleton University
 * @version 1.0, January 07th, 2025
 * @version 2.0, January 10th, 2026
 */

public class Technician implements Runnable {
    private AssemblyTable assemblyTable;                //The common table between Agent and Technicians
    private Components components;      //The only component each instance of Technician has an infinite supply of (this component is different between all three Technicians)
    private LoggerService logger;

    /**
     * Constructor for Technician
     *
     * @param t      //The common table between Agent and Technicians
     * @param i      //The component this Technician has an infinite supply of
     * @param logger
     */
    public Technician(AssemblyTable t, Components i, LoggerService logger){
        this.assemblyTable = t;
        this.components = i;
        this.logger = logger;
    }

    /**
     * Method used for each Technician thread when ran
     */
    public void run(){
        logger.log(Thread.currentThread().getName(), "INFO", null, "Waiting for remaining components...");
        while (this.assemblyTable.getDronesAssembled() != 20){   //Will loop until 20 drones have been assembled
            this.assemblyTable.getComponents(this.components); //Attempts to obtain the missing components for the Technician (if obtained, drone is assembled)
            // Sleep for between 0 and 5 seconds before calculating n!
            try {
                Thread.sleep((int)(Math.random() * 5000));
            } catch (InterruptedException e) {}
        }

        //All drones have been assembled
        logger.log(Thread.currentThread().getName(), "INFO", null, "20 drones assembled, ending...");
    }
}
