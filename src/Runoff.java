
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
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "dep storage")
    public Attribute.Double depStor;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
            description = "dep runoff")
    public Attribute.Double depRunoff;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "soil runoff")
    public Attribute.Double soilRunoff;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "base runoff")
    public Attribute.Double baseRunoff;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            description = "Parameter a",
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
         * Berechnung des Oberflächenabflusses
         */
        
        depRunoff.setValue(depStor.getValue() * a.getValue());
    }

    @Override
    public void run() {
        /***
         * Berechnung des Gesamtabflusses aus den einzel Abflüssen
         */
        
        simRunoff.setValue(depRunoff.getValue() + soilRunoff.getValue() + baseRunoff.getValue());
    }

    @Override
    public void cleanup() {
    } 
    
}
