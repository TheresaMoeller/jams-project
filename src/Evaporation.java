
import jams.data.*;
import jams.model.*;
import static java.lang.Math.E;
import java.util.ArrayList;
import java.util.List;

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
    title="Evaporation calculator",
    author="Theresa Moeller",
    description="Calculates potential evaporation",
    date = "2017-05-13",
    version = "1.0_0"
)

public class Evaporation extends JAMSComponent {
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
            description = "Old soil storage")
    public Attribute.Double soilStor;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Mean temperature")
    public Attribute.Double meantemp;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Relative humidity")
    public Attribute.Double relhum;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Maximum temperature")
    public Attribute.Double maxtemp;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Potential evaporation")
    public Attribute.Double ET;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Month of meassure")
    public Attribute.Double month;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Calibration factor")
    public Attribute.Double w;
    
    /*@JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "New soil storage")
    public Attribute.Double soilStor;*/
    
//    List<Double> k = new ArrayList<Double>();
//    Double relhum14;
    Double potEv;
    
    /*
     *  Component run stages
     */
    
    @Override
    public void init() {
        // Create List of Haude factors (landuse spruce)
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
        
        Double relhum14;
        relhum14 = (calcTemp(meantemp.getValue())*relhum.getValue()) / calcTemp(maxtemp.getValue());
        
        int x = (int) month.getValue() - 1;
        potEv = k.get(x)* calcVapourPressure(meantemp.getValue()) * (1-relhum14) * w.getValue();

    }

    @Override
    public void run() {
        ET.setValue(potEv);
        soilStor.setValue(soilStor.getValue()-potEv);
    }

    @Override
    public void cleanup() {
    }
    
    public Double calcVapourPressure(Double temp) {
        Double v = (17.62*temp)/(243.12+temp);
        Double e = 6.11 * Math.pow(E, v);
        return e;
    }
    
    public Double calcTemp(Double temp) {
        Double t = calcVapourPressure(temp)*(216.7/(temp + 273.15));
        return t;
    }
    
}
