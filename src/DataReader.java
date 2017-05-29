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
import java.util.StringTokenizer;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;


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
            description = "Input data file")
    public static Attribute.String filePath;
    
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

    static List<InputData> data = new ArrayList<InputData>();
    static int counter = 0;

    /*
     *  Component run stages,
     */
    @Override
    public void init() {
        /***
         * Read input values from given data file
         */        
        
        try {
            // Falls kein Pfad angegeben, zeige Fehler
            if (filePath == null) {
                System.out.println("Specify a path to the input file!");
            }
            File file = null;
            
            // Prüfe ob Datei existiert, zeige eventuell Fehler
            if (new File(filePath.getValue()).exists()){
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
                if (line.startsWith("0") | 
                        line.startsWith("1") | 
                        line.startsWith("2") | 
                        line.startsWith("3")){
                    StringTokenizer st = new StringTokenizer(line);
                    InputData dataset = new InputData();
                    int x = 0;
                    String token = st.nextToken();
                    List<Double> val = new ArrayList<Double>();
                    dataset.setDate(token);

                    //Füge alle restlichen Werte in die values Liste ein
                    while(st.hasMoreTokens()){
                        token = st.nextToken();
                        val.add(Double.parseDouble(token));
                        x++;
                    }
                    
                    //Füge die Liste zur ArrayList hinzu
                    dataset.setValues(val);
                    data.add(dataset);
                } 
            } 
            
            //Wenn Zeile keine Werte mehr enthält, ist das Ende erreicht
            if(line == null){
                System.out.println("Reached end of data.");
                reader.close(); //Schließe den Reader
            };
            System.out.println("data: " + data.size());
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        counter=0;
    }
    
    @Override
    public void run() {
        /***
         * Set Values from file as given values for modelling
         ***/
        
        date.setValue(data.get(counter).getDate());
        precip.setValue(data.get(counter).getValues().get(0));
        mintemp.setValue(data.get(counter).getValues().get(1));
        meantemp.setValue(data.get(counter).getValues().get(2));
        maxtemp.setValue(data.get(counter).getValues().get(3));
        sunshine.setValue(data.get(counter).getValues().get(4));
        relhum.setValue(data.get(counter).getValues().get(5));
        abshum.setValue(data.get(counter).getValues().get(6));
        windspeed.setValue(data.get(counter).getValues().get(7));
        counter++;
    }

    @Override
    public void cleanup() {
    }

}
