
/**
 * JAMS example component - can be used as template for new components
 */
/**
 *
 * @author Theresa
 */
import jams.data.*;
import jams.model.*;
import Input.InputData;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.List;
import java.util.Map;

@JAMSComponentDescription(
        title = "Data Reader for Input",
        author = "Theresa",
        description = "Reads input data for runoff model",
        date = "2017-05-08",
        version = "1.0_0"
)

public class DataReader extends JAMSComponent {

    /*
     *  Component attributes
     */
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Input data file for climate data")
    public static Attribute.String filePath;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Input data file for runoff data")
    public static Attribute.String filePathRunoff;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Month")
    public static Attribute.String date;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Precip value read from file")
    public static Attribute.Double precip;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Minumum Temperature value read from file")
    public static Attribute.Double mintemp;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Mean Temperature value read from file")
    public static Attribute.Double meantemp;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Maximum Temperature value read from file")
    public static Attribute.Double maxtemp;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Sunshine hours read from file")
    public static Attribute.Double sunshine;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Relative Humidity value read from file")
    public static Attribute.Double relhum;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Absolute Humidity value read from file")
    public static Attribute.Double abshum;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "windspeed value read from file")
    public static Attribute.Double windspeed;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "runoff value read from comparison file")
    public static Attribute.Double runoff;

    //static List<InputData> data = new ArrayList<InputData>();
    //static int counter = 0;
    Map<String, List<Double>> data = new HashMap<String, List<Double>>();

    /*
     *  Component run stages,
     */
    @Override
    public void init() {
        /**
         * Read input values from given data file
         */

        Map<String, Double> compare = readCompare();

        try {
            // Falls kein Pfad angegeben, zeige Fehler
            if (filePath == null) {
                System.out.println("Specify a path to the input file!");
            }
            File file = null;

            // Prüfe ob Datei existiert, zeige eventuell Fehler
            if (new File(filePath.getValue()).exists()) {
                file = new File(filePath.getValue());
            } else {
                System.out.println("File does not exist.");
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            // Solange die Zeile Werte enthält
            while ((line = reader.readLine()) != null) {
                System.out.println(line);

                //Prüfe ob es sich um eine Wertzeile handelt (starten mit Datumsangabe)
                if (line.startsWith("0")
                        | line.startsWith("1")
                        | line.startsWith("2")
                        | line.startsWith("3")) {
                    //Zerlege Zeile in einzelne Abschnitte (nach Whitespace)
                    StringTokenizer st = new StringTokenizer(line);
                    //Erstelle eine Liste von Werten
                    List<Double> val = new ArrayList<Double>();
                    //Setze ersten Zeilenabschnitt als Datum
                    String dat = st.nextToken();;

                    //Füge alle restlichen Werte in die Werteliste ein
                    while (st.hasMoreTokens()) {
                        val.add(Double.parseDouble(st.nextToken()));
                    }

                    //Füge Abflussvergleichswerte zur Werteliste hinzu
                    val.add(compare.get(dat));

                    //Füge Werte in Haspmap ein (nach Datum)
                    data.put(dat, val);
                }
            }

            //Wenn Zeile keine Werte mehr enthält, ist das Ende erreicht
            if (line == null) {
                System.out.println("Reached end of data.");
                reader.close(); //Schließe den Reader
            };
            System.out.println("data: " + data.size());
        } catch (FileNotFoundException ex) {
            System.out.println("File not found.");
        } catch (IOException ex) {
            System.out.println("An error occurred.");;
        }
        //counter = 0;
    }

    @Override
    public void run() {
        /**
         * *
         * Set Values from file as given values for modelling *
         */

        for (Map.Entry<String, List<Double>> entry : data.entrySet()) {
            date.setValue(entry.getKey());
            precip.setValue(entry.getValue().get(0));
            mintemp.setValue(entry.getValue().get(1));
            meantemp.setValue(entry.getValue().get(2));
            maxtemp.setValue(entry.getValue().get(3));
            sunshine.setValue(entry.getValue().get(4));
            relhum.setValue(entry.getValue().get(5));
            abshum.setValue(entry.getValue().get(6));
            windspeed.setValue(entry.getValue().get(7));
            runoff.setValue(entry.getValue().get(8));
        }
        //counter++;
    }

    @Override
    public void cleanup() {
    }

    public Map<String, Double> readCompare() {
        Map<String, Double> comp = new HashMap<String, Double>();

        try {
            // Falls kein Pfad angegeben, zeige Fehler
            if (filePathRunoff == null) {
                System.out.println("Specify a path to the comparison file!");
            }
            File file = null;

            // Prüfe ob Datei existiert, zeige eventuell Fehler
            if (new File(filePathRunoff.getValue()).exists()) {
                file = new File(filePathRunoff.getValue());
            } else {
                System.out.println("File does not exist.");
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            String date;
            Double val = 0.0;

            // Solange die Zeile Werte enthält
            while ((line = reader.readLine()) != null) {
                //Prüfe ob es sich um eine Wertzeile handelt (starten mit Datumsangabe)
                if (line.startsWith("0")
                        | line.startsWith("1")
                        | line.startsWith("2")
                        | line.startsWith("3")) {
                    StringTokenizer st = new StringTokenizer(line);
                    date = st.nextToken();

                    //Füge Abflusswerte in die values Liste ein
                    while (st.hasMoreTokens()) {
                        val = Double.parseDouble(st.nextToken());
                    }

                    comp.put(date, val);
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return comp;
    }

}
