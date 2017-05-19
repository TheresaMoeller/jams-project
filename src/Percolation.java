
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
            description = "Soil storage")
    public Attribute.Double soilStor;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Base storage")
    public Attribute.Double baseStor;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "Soil runoff")
    public Attribute.Double soilRunoff;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "base runoff")
    public Attribute.Double baseRunoff;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            description = "Parameter b",
            lowerBound = 0,
            upperBound = 1
            )
            public Attribute.Double b;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            description = "Parameter c",
            lowerBound = 0,
            upperBound = 1
            )
            public Attribute.Double c;
     
     /*
     *  Component run stages
     */
    
    @Override
    public void init() {  
    }

    @Override
    public void run() {
        soilRunoff.setValue(soilStor.getValue() * b.getValue());
        baseStor.setValue(soilStor.getValue() - soilRunoff.getValue());
        baseRunoff.setValue(baseStor.getValue() * c.getValue());
        baseStor.setValue(baseStor.getValue() - baseRunoff.getValue());
    }

    @Override
    public void cleanup() {
    } 
     
}
