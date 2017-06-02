
/**
 * JAMS example component - can be used as template for new components
 */
/**
 *
 * @author Theresa
 */
import jams.data.*;
import jams.model.*;
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
        description = "Reads input climate data and comparison runoff data for runoff model",
        date = "2017-05-08",
        version = "1.0_0"
)

public class DataReader extends JAMSComponent {

    /*
     *  Component attributes
     */
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Input data file for climate data")
    public Attribute.String filePath;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Input data file for runoff data")
    public Attribute.String filePathRunoff;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Date of meassure")
    public Attribute.Calendar date;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Precipitation data column",
            lowerBound = 1)
    public Attribute.Integer precipColumn;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Minimum temperature data column",
            lowerBound = 1)
    public Attribute.Integer mintempColumn;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Mean temperature data column",
            lowerBound = 1)
    public Attribute.Integer meantempColumn;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Maximum temperature data column",
            lowerBound = 1)
    public Attribute.Integer maxtempColumn;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Relative humidity data column",
            lowerBound = 1)
    public Attribute.Integer relhumColumn;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Runoff data column",
            lowerBound = 1)
    public Attribute.Integer obsQColumn;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Precip value read from file",
            unit = "mm",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double precip;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Minimum Temperature value read from file",
            unit = "°C",
            lowerBound = Double.NEGATIVE_INFINITY,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double mintemp;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Mean Temperature value read from file",
            unit = "°C",
            lowerBound = Double.NEGATIVE_INFINITY,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double meantemp;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Maximum Temperature value read from file",
            unit = "°C",
            lowerBound = Double.NEGATIVE_INFINITY,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double maxtemp;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Relative Humidity value read from file",
            unit = "%",
            lowerBound = 0,
            upperBound = 100)
    public Attribute.Double relhum;

//    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
//            description = "Absolute Humidity value read from file")
//    public Attribute.Double abshum;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Runoff value read from comparison file",
            unit = "m³/s",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double obsQ;

    //int counter = 0;
    Map<String, List<Double>> climate;

    /*
     *  Component run stages,
     */
    @Override
    public void init() {
        /**
         * Read input values from given data files
         */

        climate = readData(filePath); //read climate data
        Map<String, List<Double>> ro = readData(filePathRunoff); //read runoff data

        //iterate over all mappings in climate data
        for (Map.Entry<String, List<Double>> entry : climate.entrySet()) {
            String key = entry.getKey(); //get date of climate data
            Double v = ro.get(key).get(obsQColumn.getValue()-1); //get value of runoff for that date
            entry.getValue().add(v); //append the runoff value to the climate data
        }
        
        //print size of the dataset (count of dates)
        this.getModel().getRuntime().println("Dataset size is " + climate.size());
    }

    @Override
    public void run() {
        /**
         * *
         * Set Values from file as given values for modelling *
         */

        try {
            //iterate over all mappings (climate and runoff)
            for (Map.Entry<String, List<Double>> entry : climate.entrySet()) {
                //set values for model parameter
                precip.setValue(entry.getValue().get(precipColumn.getValue()-1));
                mintemp.setValue(entry.getValue().get(mintempColumn.getValue()-1));
                meantemp.setValue(entry.getValue().get(meantempColumn.getValue()-1));
                maxtemp.setValue(entry.getValue().get(maxtempColumn.getValue()-1));
                relhum.setValue(entry.getValue().get(relhumColumn.getValue()-1));
                obsQ.setValue(entry.getValue().get(obsQColumn.getValue()-1));
            }
        } catch (NullPointerException e){
            getModel().getRuntime().sendHalt("Dataset is empty.");
        }

        //counter++;
    }

    @Override
    public void cleanup() {
    }

    public Map<String, List<Double>> readData(Attribute.String path) {
        this.getModel().getRuntime().println("--- Reading from file " + path.getValue() + " ---");
        
        BufferedReader reader = null;
        Map<String, List<Double>> data = new HashMap<String, List<Double>>();

        try {
            File file = null;

            //Prüfe ob Datei existiert, zeige eventuell Fehler
            if (new File(path.getValue()).exists()) {
                file = new File(path.getValue());
            } else {
                getModel().getRuntime().sendHalt("File does not exist.");
            }

            //BufferedReader 
            reader = new BufferedReader(new FileReader(file));
            String line;
            boolean startCollect = false;

            // Solange die Zeile Werte enthält
            while ((line = reader.readLine()) != null) {
                String dat;
                List<Double> val;

                //Prüfe ob es sich um eine Wertzeile handelt (alle Zeilen nach #start)
                if (line.contains("#start")) {
                    startCollect = true;
                    continue;
                }

                if (line.contains("#end")) {
                    startCollect = false;
                }

                if (startCollect) {
                    //Zerlege Zeile in einzelne Abschnitte (nach Whitespace)
                    StringTokenizer st = new StringTokenizer(line);
                    //Erstelle eine Liste von Werten
                    val = new ArrayList<Double>();
                    //Setze ersten Zeilenabschnitt als Datum
                    dat = st.nextToken();

                    //Füge alle restlichen Werte in die Werteliste ein
                    while (st.hasMoreTokens()) {
                        val.add(Double.parseDouble(st.nextToken()));
                    }
                    //Füge Werte in Haspmap ein (nach Datum)
                    data.put(dat, val);
                }
            }
            reader.close(); //Schließe den Reader
        } catch (FileNotFoundException ex) {
            getModel().getRuntime().sendHalt("File not found.");
        } catch (IOException ex) {
            getModel().getRuntime().sendHalt("An error occured.");
        }
        //counter = 0;
        return data;
    }
}
