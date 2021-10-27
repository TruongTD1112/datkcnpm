package algorithms.planningpoker;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class PlanningPoker {
    /**
     * Constructor method.
     *
     * @param csvFile file CSV data.
     */
    public PlanningPoker(String csvFile) {
        this.csvFile = csvFile;
    }

    /**
     * All needed attr
     */
    String csvFile;


    /**
     * @param csvFile
     * @return ArrayList(String[])
     */
    public static ArrayList<String[]> portData(String csvFile) {
        ArrayList<String[]> data = new ArrayList<>();
        try {
            Reader reader = new FileReader(csvFile);
            CSVReader csvReader = new CSVReader(reader);
            // Reading Records One by One in a String array
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                data.add(line);
            }
        } catch (
                IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static Float[] calculateAvarageProb(ArrayList<String[]> data) {
        Float[] Coordinates = new Float[data.size()];
//        System.out.println("Coord-size : "+Coordinates.length);
//        System.out.println("Coord-size : "+Coordinates[6]);
        //calculate average number
        for (int i = 0; i < data.size(); i++) {
            float temp = 0;
            for (int j = 1; j < data.get(i).length; j++) {                  //FAKE NUMBER//
                temp += Float.parseFloat(data.get(i)[j]);
                System.out.print(temp + " - ");
            }
            Coordinates[i] = temp / data.get(i).length / 100;
            System.out.println(Coordinates[i]);
        }
        //calculate coordinates
        for (int i = 0; i < Coordinates.length; i++) {
            Coordinates[i] = 700 * (1 - Coordinates[i]) + 100;
        }
        return Coordinates;
    }

}
