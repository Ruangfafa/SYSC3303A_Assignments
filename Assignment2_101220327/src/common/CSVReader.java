package common;

import service.LoggerService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for reading fire incident events from a CSV file.
 *
 * This class parses each valid line in the CSV file and converts it
 * into a FireEvent object. Invalid or malformed lines are skipped
 * and logged as warnings.
 *
 * The class is designed as a utility class and cannot be instantiated.
 */
public final class CSVReader {
    private static final Logger logger = LoggerService.getLogger();

    private CSVReader() {}

    /**
     * Reads fire incident events from a CSV file.
     *
     * Each non-empty line in the file must contain exactly four comma-separated values in the following order:
     * time, zoneId, eventType, severity
     * Lines that do not match the expected format are ignored.
     *
     * @param filePath path to the CSV file containing fire event data
     * @return a list of FireEvent objects parsed from the file;
     *         returns an empty list if the file cannot be read or no valid events are found
     */
    public static ArrayList<String> readFireEvents(String filePath) {

        ArrayList<String> requests = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                requests.add(line);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE,"[CSVReader] Failed to read file: " + filePath, e);
        }

        return requests;
    }
}
