
import jams.data.*;
import jams.model.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Theresa
 */
public class DataWriter extends JAMSComponent {

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Path to output file",
            defaultValue = "output.txt")
    public Attribute.FileName outfile;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Date of meassure")
    public Attribute.Calendar date;
    
//    @JAMSVarDescription (access = JAMSVarDescription.AccessType.READ, ??//eingefügt, okay?
//                         description = "Aktueller Zeitschritt im Modell ??")
//    public Attribute.?? time;
     
//    @JAMSVarDescription (access = JAMSVarDescription.AccessType.READ, ??//eingefügt, okay?
//                         description = "Aktuelles Zeitintervall im Modell (ist das Zeit-"
//                                 + "intervall monatlich oder täglich) ?")
//    public Attribute.Calendar timeInt;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Precip value read from file",
            unit = "mm",
            lowerBound = Double.NEGATIVE_INFINITY,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double precip;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Soil storage",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double soilStor;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "runoff value read from comparison file",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double obsRunoff;

    @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READ, 
            description = "Simulated runoff",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY
    )
    public Attribute.Double simRunoff;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Snow Storage",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double snowStor;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Base Storage",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double baseStor;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Amount of snowmelt",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double snowMelt;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "effective amount of snow melt",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double eff_snowMelt;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Potential Evaporation",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double potET;

    Map<String, List<Double>> data = new TreeMap<String, List<Double>>();

    @Override
    public void init() {        
    }

    @Override
    public void run() {
         //create list of all values that should be written to file
        List<Double> values = new ArrayList<Double>();
        values.add(precip.getValue());                      //precipitation
        values.add(obsRunoff.getValue());                   //observed runoff
        values.add(simRunoff.getValue());                   //simulated runoff
        values.add(snowStor.getValue());                    //snow storage
        values.add(snowMelt.getValue());                    //snow melt
        values.add(eff_snowMelt.getValue());                //effective snow melt
        values.add(baseStor.getValue());                    //base storage
        values.add(precip.getValue()-obsRunoff.getValue()); //real evaporation as difference between precipitation and runoff
        values.add(potET.getValue());                       //potential evaporation

        //? time, timeInt addieren?;
                
        //Speichere Datum und Werte in Hashmap mit Datum als Schlüssel
        
        data.put(date.toString().split(" ")[0], values);
    }

    @Override
    public void cleanup() {
        
        write2File(outfile, data);
    }

    public void write2File(Attribute.String out, Map<String, List<Double>> content){
        /***
         * write specified map content to text file
         */
        
        this.getModel().getRuntime().println("--- Writing to file " + out.getValue() + " ---");

        //create new file
        File file = new File(this.getModel().getWorkspaceDirectory(), out.getValue());

        try {
            file.createNewFile();
        } catch (IOException ex) {
            getModel().getRuntime().sendHalt("An error occured. Couldn't create file " + out.getValue() + ". Aborting.");
        }

        //create write to write to file
        BufferedWriter bw = null;
        FileWriter fw = null;

        //set date format (date appears in file header)
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        LocalDateTime current = LocalDateTime.now();

        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);

            //write header
            bw.write("#Results of JAMS Runoff Model");
            bw.newLine();
            bw.write("#date of calculation: " + dtf.format(current));
            bw.newLine();
            bw.write("#date percip obsRunoff simRunoff snowStor snowMelt eff_snowMelt baseStor realET potET");
            bw.newLine();
            bw.write("#start");
            bw.newLine();

            //iterate over all mappings in conent
            for (Map.Entry<String, List<Double>> entry : data.entrySet()) {
                //write date
                bw.write(entry.getKey() + " ");
                //iterate over values (list brakets are not written to file)
                for (Double val:entry.getValue()){
                    //write value and seperate with white space
                    bw.write(val.toString() + " ");
                }
                //jump to new line
                bw.newLine();
            }
            
            bw.write("#end");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        //close all writers
        try {
            bw.close();
            fw.close();
        } catch (IOException ex) {
            getModel().getRuntime().sendHalt("Could not close Writer.");
        }
    }
    
}
