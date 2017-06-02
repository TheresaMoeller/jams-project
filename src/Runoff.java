
import jams.data.*;
import jams.model.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Theresa
 */
public class Runoff extends JAMSComponent{
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
            description = "depression storage",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double depStor;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
            description = "dep runoff",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double depRunoff;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "soil runoff",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double soilRunoff;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "base runoff",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY)
    public Attribute.Double baseRunoff;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            description = "Calibration factor surface runoff",
            lowerBound = 0,
            upperBound = 1
            )
            public Attribute.Double a;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.WRITE,
            description = "Simulated runoff",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY
            )
            public Attribute.Double simRunoff;
    
    /*
     *  Component run stages
     */
    
    @Override
    public void init() {  
        /***
         * Status print
         */
        this.getModel().getRuntime().println("--- Runoff Component is running ---");
    }

    @Override
    public void run() {
        /***
         * calculation of simulated runoff
         */
        
        //calculate depression runoff
        depRunoff.setValue(depStor.getValue() * a.getValue());
        //calculate over all runoff
        simRunoff.setValue(depRunoff.getValue() + soilRunoff.getValue() + baseRunoff.getValue());
    }

    @Override
    public void cleanup() {
    } 
    
}
