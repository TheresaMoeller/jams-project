import jams.JAMS;
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
            description = "time interval, for which the model should be executed" //Zeitschritt und Zeitraum des Modells
    )
    public Attribute.?? timeInt //? 
 

@JAMSVarDescription(
        access = JAMSVarDescription.AccessType.READ,
        description = "Filling of the snow storage at beginning of model run",
        lowerBound = 0,
        upperBound = Double.POSITIVE_INFINITY,
        unit = "mm"
    )
    public Attribute.Double initSnowStor; 
        

@JAMSVarDescription(
        access = JAMSVarDescription.AccessType.READ,
        description = "Precipitation as snow",
        unit = "mm",
        lowerBound = 0,
        upperBound = Double.POSITIVE_INFINITY
    )
    public Attribute.Double precip_snow;


@JAMSVarDescription(
            access = JAMSVarDescription.AccessType.WRITE,
            description = "Potential amount of snowmelt/ snowmelt rate",
            unit = "mm/d",
            lowerBound = Double.NEGATIVE_INFINITY,
            upperBound = Double.POSITIVE_INFINITY
    )
    public Attribute.Double snowMelt;


@JAMSVarDescription(
        access = JAMSVarDescription.AccessType.WRITE,
        description = "Filling of the snow storage after time step",
        lowerBound = 0,
        upperBound = Double.POSITIVE_INFINITY,
        unit = "mm"
    )
    public Attribute.Double snowStor;  


@JAMSVarDescription(
        access = JAMSVarDescription.AccessType.WRITE,
        description = "Effective amount of snowmelt/ snowmelt rate",
        lowerBound = 0,
        upperBound = Double.POSITIVE_INFINITY,
        unit = "mm"
    )
    public Attribute.Double eff_snowMelt;
 

    private double timeunit; ? //Variable zum Speichern des Zeitintervalls im Modell ?
 
   /*
     *  Component run stages
     */
    
      
    @Override
    public void init() {
       
        timeunit = this.timeInt.getTimeUnit;          //Zeitintervall (täglich oder monatlich
        
        if (this.initSnowStor == null)
            getModel().getRuntime().sendHalt(JAMS.i18n("parameter initSnowStor unspecified"));
        else
            snowStor.setValue(initSnowStor.getValue());
        
    }
 
    @Override
    public void run() {
        
        if (this.precip_snow == null)
            getModel().getRuntime().sendHalt(JAMS.i18n("input data precip_snow unspecified"));
        
        if (this.snowMelt == null)
            getModel().getRuntime().sendHalt(JAMS.i18n("parameter snowMelt unspecified"));
        
        double bT = this.baseTemp.getValue();
        double meanT = this.meantemp.getValue();
        double maxT = this.maxtemp.getValue();
        double DDF = this.ddf.getValue();
        double meltT = this.meltTemp.getValue();
        double averageT = 0.5 * (meanT + maxT); // daily average temperature (mean of meanT and maxT)
        double snowmelt_amount;
        double snowStor = this.snowStor.getValue();
        double snow = this.precip_snow.getValue();
        double snowMelt = this.snowMelt.getValue();
        double snowMelt_eff;
         
        // If the average temperture is higher than the melting temperature, 
        // then the potential snowmelt amount can be calculated by using ddf, 
        // the base temperature and the average temperature
        // http://www.tandfonline.com/doi/pdf/10.1080/02626668909491333
        
        if (averageT > meltT){
        snowmelt_amount = DDF *(averageT - bTemp);   
        }
        else {
        snowmelt_amount = 0;    
        }
        
                     
        // Calculation snowStor and snowMelt_eff from snowMelt and precip_snow 
        snowStor = snowStor + snow;
        
        if (snowMelt <= snowStor) { //some snow of the snowStor melts
            snowMelt_eff = snowMelt;
            snowStor = snowStor - snowMelt;
        } 
        else { //whole snowStor melts
            snowMelt_eff = snowStor;
            snowStor = 0;
        }
        
        //write values
        if Zeitintervall (tu) ist täglich { ?//wie definieren?
            snowMelt.setValue(snowmelt_amount); // potenzielle Verdunstungsmenge
        }
        else if Zeitintervall (tu) ist monatlich {  ?//wie definieren?
            snowMelt.setValue(snowmelt_amount * this.time.getActualMaximum(Attribute.??)); ?? //tägliche Werte irgendwie summieren?
        }
       
        this.snowStor.setValue(snowStor);
        this.eff_snowMelt.setValue(snowMelt_eff);
    }

    @Override
    public void cleanup() {
    }
}


?// später für Infiltration: Schneeschmelze zu Boden- und Muldenspeicher dazurechnen