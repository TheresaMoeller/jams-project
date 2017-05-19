/**
 * JAMS example component - can be used as template for new components
 */

import jams.data.*;
import jams.model.*;

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
import java.util.StringTokenizer;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;


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
    public static Attribute.Double month;
    
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

    static ArrayList<double[]> data = new ArrayList<double[]>();
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
                    double dataset[] = new double[9];
                    int x = 1;
                    String token = st.nextToken().split(".")[1];
                    //Monat auslesen
                    if (token.startsWith("0")){
                        CharacterIterator cIter = new StringCharacterIterator(token) ;
                        char ch = cIter.last() ;
                        dataset[0] = (double) Character.digit(ch, 10);
                    } else {
                        dataset[0] = Double.parseDouble(token);
                    }
                    //Bis zum Ende der Zeile trage alle Werte einzeln in die Liste dataset ein
                    while(st.hasMoreTokens()){
                        token = st.nextToken();
                        dataset[x] = Double.parseDouble(token);
                        x++;
                    }
                    //Füge die Liste zur ArrayList hinzu
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
        
        month.setValue(data.get(counter)[0]);
        precip.setValue(data.get(counter)[1]);
        mintemp.setValue(data.get(counter)[2]);
        meantemp.setValue(data.get(counter)[3]);
        maxtemp.setValue(data.get(counter)[4]);
        sunshine.setValue(data.get(counter)[5]);
        relhum.setValue(data.get(counter)[6]);
        abshum.setValue(data.get(counter)[7]);
        windspeed.setValue(data.get(counter)[8]);
        counter++;
    }

    @Override
    public void cleanup() {
    }

}
