
import jams.data.*;
import jams.model.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "windspeed value read from file")
    public static Attribute.FileName outfile;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Month")
    public static Attribute.String date;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Precip value read from file")
    public static Attribute.Double precip;

    @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READ, //gemessener Abfluss?
            description = "Simulated runoff",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY
    )
    public Attribute.Double simRunoff;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Precip value read from file")
    public static Attribute.Double snowStor;

    // Attribut für Schneeschmelze
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Precip value read from file")
    public static Attribute.Double baseStor;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ, //aktuelle Verdunstung
            description = "Precip value read from file")
    public static Attribute.Double ET;

    Map<String, List<Double>> num = new HashMap<String, List<Double>>();

    @Override
    public void init() {

        //Erstelle Liste aller notwendigen Werte
        List<Double> values = new ArrayList<Double>();
        values.add(precip.getValue());
        values.add(simRunoff.getValue());
        values.add(snowStor.getValue());
        values.add(baseStor.getValue());
        values.add(ET.getValue());

        //Speichere Datum und Werte in Hashmap mit Datum als Schlüssel
        num.put(date.toString(), values);
    }

    @Override
    public void run() {

        File file = new File(this.getModel().getWorkspaceDirectory(), outfile.getValue());

        //neue Datei erzeugen
        try {
            file.createNewFile();
        } catch (IOException ex) {
            getModel().getRuntime().sendHalt("An error occured. Couldn't create file " + outfile.getValue() + ". Aborting.");
        }

        // Writer anlegen um Datei zu beschreiben
        BufferedWriter bw = null;
        FileWriter fw = null;

        //Datumsformat festlegen (Datum erscheint im Dateikopf)
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime current = LocalDateTime.now();

        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);

            //Schreibe Kopf der Outputdatei
            bw.write("# Results of JAMS Runoff Model");
            bw.newLine();
            bw.write("#date of calculation: " + dtf.format(current));
            bw.newLine();
            bw.write("#date percip simRunoff runoff snowStor snowMelt baseStor potET realET");
            bw.newLine();

            //Iteration über alle Map-Elemente
            for (Map.Entry<String, List<Double>> entry : num.entrySet()) {
                bw.write(entry.getKey() + " " + entry.getValue());
                bw.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        try {
            bw.close();
            fw.close();
        } catch (IOException ex) {
            getModel().getRuntime().sendHalt("Could not close Writer.");
        }

    }

    @Override
    public void cleanup() {
    }

}
