import jams.data.*;
import jams.model.*;

/**
 *
 * @author Anna-Lena
 */
 @JAMSComponentDescription(
    title="Snowmelt Calculator",
    author="Anna-Lena Seith",
    description="Calculates potential snow melt amount based on the degree-day-method",
    date = "2017-05-31",
    version = "1.0_0"
)
@VersionComments(entries = {
    @VersionComments.Entry(version = "1.0_0", comment = "Initial version"),
    @VersionComments.Entry(version = "1.0_1", comment = "Some improvements")
})        
public class Snowmelt extends JAMSComponent {

    /*
     *  Component attributes
     */
    
    @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READ,       
            description = "Temperature limit value for snowfall, calibration factor",                                                   
            unit = "°C",                                     
            lowerBound = Double.NEGATIVE_INFINITY,                                      
            upperBound = Double.POSITIVE_INFINITY                                                                          
    )
            public Attribute.Double baseTemp;              

    
    @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READ,
            description = "Mean day temperature",
            unit = "°C",
            lowerBound = Double.NEGATIVE_INFINITY,
            upperBound = Double.POSITIVE_INFINITY
    )
            public Attribute.Double meantemp;
    
    
    @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READ,
            description = "Maximum day temperature",
            unit = "°C",
            lowerBound = Double.NEGATIVE_INFINITY,
            upperBound = Double.POSITIVE_INFINITY
    )
    public Attribute.Double maxtemp;
    
    
    @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READ,
            description = "Melting temperature, calibration factor",
            unit = "°C",
            lowerBound = Double.NEGATIVE_INFINITY,
            upperBound = Double.POSITIVE_INFINITY
    )
    public Attribute.Double meltTemp;
    
    
    @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READ,
            description = "day-degree-factor, empirically generated thawing "
                          + "coefficient, factor for coniferous forest: 1,5 - 2,"
                          + "calibration factor",
            unit = "mm/d °C",
            lowerBound = Double.NEGATIVE_INFINITY,
            upperBound = Double.POSITIVE_INFINITY
    )
   public Attribute.Double ddf; 
     
    
    @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READ,
            description = //Aktueller Zeitschritt im Modell
    )
    public Attribute.?? time  //?
 
            
@JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READ,
            description = //Zeitschritt und Zeitraum des Modells
    )
    public Attribute.?? timeInt //? 
 
        
@JAMSVarDescription(
            access = JAMSVarDescription.AccessType.WRITE,
            description = "Amount of snowmelt",
            unit = "mm/d",
            lowerBound = Double.NEGATIVE_INFINITY,
            upperBound = Double.POSITIVE_INFINITY
    )
    public Attribute.Double snowMelt;
 
 
    private double timeunit; ? //Variable zum Speichern des Zeitintervalls im Modell ?
 
   /*
     *  Component run stages
     */
    
      
    @Override
    public void init() {
       
        timeunit = this.timeInt.getTimeUnit;         
    }
 
    @Override
    public void run() {
        double bT = this.baseTemp.getValue();
        double meanT = this.meantemp.getValue();
        double maxT = this.maxtemp.getValue();
        double DDF = this.ddf.getValue();
        double meltT = this.meltTemp.getValue();
        double averageT = 0.5 * (meanT + maxT); // daily average temperature (mean of meanT and maxT)
        double snowmelt_amount;
         
        // If the average temperture is higher than the melting temperature, 
        // then the potential snowmelt amount can be calculated by using ddf and the average temperature
        // Die Berechnung erfolgt mithilfe der Basistemperatur? nochmal nachschauen!
        
        if (averageT > meltT){
        snowmelt_amount = DDF *(averageT - bTemp);    ??nochmal nachschauen ;
        }
        else {
        snowmelt_amount = 0;    
        }
         
        if Zeitintervall (tu) ist täglich { ?//wie definieren?
            snowMelt.setValue(snowmelt_amount); // potenzielle Verdunstungsmenge
        }
        else if Zeitintervall (tu) ist monatlich {  ?//wie definieren?
            snowMelt.setValue(snowmelt_amount * this.time.getActualMaximum(Attribute.??)); ?? //tägliche Werte irgendwie summieren?
        }
         
    }

    @Override
    public void cleanup() {
    }
}