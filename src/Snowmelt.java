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
     
//    
//    @JAMSVarDescription(
//            access = JAMSVarDescription.AccessType.READ,
//            description = //Aktueller Zeitschritt im Modell
//    )
//    public Attribute.?? time  //? Kalendervariable?
// 
//            
//@JAMSVarDescription(
//            access = JAMSVarDescription.AccessType.READ,
//            description = "time interval, for which the model should be executed" //Zeitschritt und Zeitraum des Modells
//    )
//    public Attribute.?? timeInt //? 
 
        
@JAMSVarDescription(
        access = JAMSVarDescription.AccessType.READ,
        description = "Saturation value of SoilStorage. If value is exceeded, the "
                        + "water flows into the depression storage",
        lowerBound = 0,
        upperBound = Double.POSITIVE_INFINITY,
        unit = "mm"
        )
        public Attribute.Double satValue;    //Kalibrationsfaktor
        

@JAMSVarDescription(
            access = JAMSVarDescription.AccessType.WRITE,
            description = "Potential amount of snowmelt/ snowmelt rate",
            unit = "mm/d",
            lowerBound = Double.NEGATIVE_INFINITY,
            upperBound = Double.POSITIVE_INFINITY
    )
    public Attribute.Double snowMelt;


@JAMSVarDescription(
        access = JAMSVarDescription.AccessType.READWRITE,
        description = "Filling of the snow storage after time step",
        lowerBound = 0,
        upperBound = Double.POSITIVE_INFINITY,
        unit = "mm"
    )
    public Attribute.Double snowStor;  


@JAMSVarDescription(
        access = JAMSVarDescription.AccessType.READWRITE,
        description = "Filling of the soil storage after time step",
        lowerBound = 0,
        upperBound = Double.POSITIVE_INFINITY,
        unit = "mm"
        )
        public Attribute.Double soilStor; 


@JAMSVarDescription(
        access = JAMSVarDescription.AccessType.READWRITE,
        description = "Filling of the depression storage after time step",
        lowerBound = 0,
        upperBound = Double.POSITIVE_INFINITY,
        unit = "mm"
        )
        public Attribute.Double depStor;  
        
        
@JAMSVarDescription(
        access = JAMSVarDescription.AccessType.WRITE,
        description = "Effective amount of snowmelt/ snowmelt rate",
        lowerBound = 0,
        upperBound = Double.POSITIVE_INFINITY,
        unit = "mm"
    )
    public Attribute.Double eff_snowMelt;
 

//    private double timeunit; ? //Variable zum Speichern des Zeitintervalls im Modell ?
 
   /*
     *  Component run stages
     */
    
      
    @Override
    public void init() {
       
//        timeunit = this.timeInt.getTimeUnit;          //Zeitintervall (täglich oder monatlich), brauch ich das überhaupt?
//        
//        //Check if needed values are existing + set initial storages before first model run
//        if (this.initSnowStor == null)
//            getModel().getRuntime().sendHalt(JAMS.i18n("parameter initSnowStor unspecified"));
//        else
//            snowStor.setValue(initSnowStor.getValue());
//        
//        if (this.initSoilStor == null)   ?//raus?
//            getModel().getRuntime().sendHalt(JAMS.i18n("parameter_Precipitation_initSoilStor_unspecified"));
//        else
//            soilStor.setValue(initSoilStor.getValue());
//        
//        if (this.initDepStor == null)   ?//raus?
//            getModel().getRuntime().sendHalt(JAMS.i18n("parameter_Precipitation_initDepStor_unspecified"));
//        else
//            depStor.setValue(initDepStor.getValue());
//        
//        if (this.satValue == null)  ?//raus?
//            getModel().getRuntime().sendHalt(JAMS.i18n("parameter_Precipitation_satValue_unspecified"));
     }
 
    @Override
    public void run() {
        
//        // if there are no values for a time step            
//        if (this.snowMelt == null)
//            getModel().getRuntime().sendHalt(JAMS.i18n("parameter Snowmmelt_snowMelt unspecified"));
//        
        // Take given values
        double bT = this.baseTemp.getValue();
        double meanT = this.meantemp.getValue();
        double maxT = this.maxtemp.getValue();
        double DDF = this.ddf.getValue();
        double meltT = this.meltTemp.getValue();
        double averageT = 0.5 * (meanT + maxT); // daily average temperature (mean of meanT and maxT)
        double snowmelt_amount;
//        double snowStor = this.snowStor.getValue();
//        double depStor = this.depStor.getValue(); ?//raus?
//        double soilStor = this.soilStor.getValue(); ?//raus?
//        double snowMelt = this.snowMelt.getValue();
//        double sat = this.satValue.getValue(); ? //raus?
        double snowMelt_eff;
         
        // If the average temperture is higher than the melting temperature, 
        // then the potential snowmelt amount can be calculated by using ddf, 
        // the base temperature and the average temperature
        // http://www.tandfonline.com/doi/pdf/10.1080/02626668909491333
        
        if (averageT > meltT){
        snowmelt_amount = DDF *(averageT - bT);   
        }
        else {
        snowmelt_amount = 0;    
        }
        
                     
        // Calculation of snowStor (and snowMelt_eff)
                
        if (snowMelt.getValue() <= snowStor.getValue()) { //amount of potential snow melt amount is less or equal than the snowStor
            snowMelt_eff = snowMelt.getValue(); //all of potential snow melt rate can effectively melt
            snowStor.setValue(snowStor.getValue() - snowMelt.getValue()); //snowStor reduces to by the snowMelt
        } 
        else { //potential snow melt rate is higher than the snowStor
            snowMelt_eff = snowStor.getValue(); //only as much snow as stored can melt
            snowStor.setValue(0); //whole snowStor has melted
        }
        
        // Calculation of soilStor and depStor
        if (soilStor.getValue() < satValue.getValue()){   //if soilStor is unsaturated
            soilStor.setValue(soilStor.getValue() + snowMelt_eff);
            if (soilStor.getValue() >= satValue.getValue()){  //if soilStor is saturated afterwards 
                depStor.setValue(depStor.getValue() + (soilStor.getValue() - satValue.getValue())); //excess water flows into depStor
                soilStor.setValue(satValue.getValue()); //soilStor gets saturation value
            }            
        }
        else {  //if soilStor is already saturated 
            depStor.setValue(depStor.getValue() + snowMelt_eff); 
            soilStor.setValue(satValue.getValue());  //soilStor gets saturation value
        }
        
//        
//        //write values
//        if Zeitintervall (tu) ist täglich { ?//wie definieren? brauch ich das überhaupt?
//            snowMelt.setValue(snowmelt_amount); 
//        }
//        else if Zeitintervall (tu) ist monatlich {  ?//wie definieren?
//            snowMelt.setValue(snowmelt_amount * this.time.getActualMaximum(Attribute.??)); ?? //tägliche Werte irgendwie summieren?
//        }
       
//        this.snowStor.setValue(snowStor);
          this.eff_snowMelt.setValue(snowMelt_eff);
//        this.soilStor.setValue(soilStor); 
//        this.depStor.setValue(depStor);   
    }

    @Override
    public void cleanup() {
    }
}

