
import jams.data.*;
import jams.model.*;
import static java.lang.Math.E;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Theresa
 */
@JAMSComponentDescription(
        title = "Evaporation calculator",
        author = "Theresa Moeller",
        description = "Calculates potential evaporation",
        date = "2017-05-13",
        version = "1.0_0"
)

public class Evaporation extends JAMSComponent {

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Date of meassure")
    public Attribute.Calendar date;
    
    @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READWRITE,
            description = "Soil Storage",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY          
            )
    public Attribute.Double soilStor;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Mean temperature",
            unit = "°C",
            lowerBound = Double.NEGATIVE_INFINITY,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double meantemp;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Maximum temperature",
            unit = "°C",
            lowerBound = Double.NEGATIVE_INFINITY,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double maxtemp;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Relative humidity")
    public Attribute.Double relhum;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Potential evaporation",
            unit = "mm/d",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double potET;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Calibration factor",
            lowerBound = 0,
            upperBound = 1)
    public Attribute.Double w;

    Double potEv;

    /*
     *  Component run stages
     */
    @Override
    public void init() {
        /***
        * Calculation of potential evaporation (HAUDE)
        */
        
        this.getModel().getRuntime().println("--- Starting evaporation calculation ---");
        
        // Create List of Haude factors (only for landuse spruce)
        List<Double> k = new ArrayList<Double>();
        k.add(0.08); //january
        k.add(0.04); //february
        k.add(0.14); //march
        k.add(0.35); //april
        k.add(0.39); //may
        k.add(0.34); //june
        k.add(0.31); //july
        k.add(0.25); //august
        k.add(0.2);  //september
        k.add(0.13); //october
        k.add(0.07); //november
        k.add(0.05); //december

        //calculate relative humidity at 2pm
        Double relhum14;
        relhum14 = (calcTemp(meantemp.getValue()) * relhum.getValue()) / calcTemp(maxtemp.getValue());
        
        //get month of meassurement
        int mon = date.get(Calendar.MONTH);

        //calculate potential evaporation
        potEv = k.get(mon) * calcVapourPressure(meantemp.getValue()) * (1 - relhum14) * w.getValue();

    }

    @Override
    public void run() {
        /***
         * Setting value of potential evaporation, reducing old soil storage
         */ 
        potET.setValue(potEv);
        soilStor.setValue(soilStor.getValue() - potEv);
    }

    @Override
    public void cleanup() {
    }

    public Double calcVapourPressure(Double temp) {
        /***
         * Calculating vapour pressure
         */
        
        Double v = (17.62 * temp) / (243.12 + temp);
        Double e = 6.11 * Math.pow(E, v);
        return e;
    }

    public Double calcTemp(Double temp) {
        /***
         * Calculating temperature for  ETP calculation
         */
        
        Double t = calcVapourPressure(temp) * (216.7 / (temp + 273.15));
        return t;
    }

}
