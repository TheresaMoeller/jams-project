
import jams.data.*;
import jams.model.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author John Doe
 */
 @JAMSComponentDescription(
    title="Percolation calculator",
    author="Theresa Moeller",
    description="Calculates the value for percolation process",
    date = "2017-05-15",
    version = "1.0_0"
)
 
public class Percolation extends JAMSComponent{
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
            description = "Soil storage",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double soilStor;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Base storage",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double baseStor;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Soil runoff",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double soilRunoff;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "base runoff",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double baseRunoff;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            description = "Calibration factor soil runoff",
            lowerBound = 0,
            upperBound = 1
            )
            public Attribute.Double b;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            description = "Calibration factor base runoff",
            lowerBound = 0,
            upperBound = 1
            )
            public Attribute.Double c;
     
     /*
     *  Component run stages
     */
    
    @Override
    public void init() {  
        this.getModel().getRuntime().println("--- Percolation Component is running ---");
    }

    @Override
    public void run() {
        /***
         * Setzen und Reduktion der Speicherwerte und Abflüsse
         */
        
        //wird der Bodenspeicher 0?
        soilRunoff.setValue(soilStor.getValue() * b.getValue()); //calculate soil runoff
        
        //geht alles aus soil in base? weiterer Kalibrierungsfaktor?
        baseStor.setValue(soilStor.getValue() - soilRunoff.getValue()); //calculate base storage
        baseRunoff.setValue(baseStor.getValue() * c.getValue()); //calculate base runoff
        baseStor.setValue(baseStor.getValue() - baseRunoff.getValue()); //update base storage
    }

    @Override
    public void cleanup() {
    } 
     
}
