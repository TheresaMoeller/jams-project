import jams.JAMS;
import jams.data.*;
import jams.model.*;

/**
 *
 * @author Anna-Lena
 */
 @JAMSComponentDescription(
    title="Precipitation Calculator",
    author="Anna-Lena Seith",
    description="Calculates water and snow components based on a linear function",
    date = "2017-05-30",
    version = "1.0_0"
)
@VersionComments(entries = {
    @VersionComments.Entry(version = "1.0_0", comment = "Initial version"),
    @VersionComments.Entry(version = "1.0_1", comment = "Some improvements")
})        
public class Precipitation extends JAMSComponent {

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
            description = "Transition temperature zone between snow and rain",
            lowerBound = Double.NEGATIVE_INFINITY,
            upperBound = Double.POSITIVE_INFINITY
    )
            public Attribute.Double transTemp;
    
  
    @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READ,
            description = "Minimum day temperature",
            unit = "°C",
            lowerBound = Double.NEGATIVE_INFINITY,
            upperBound = Double.POSITIVE_INFINITY
    )
    public Attribute.Double mintemp;

    
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
            description = "Precipitation",
            unit = "mm",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY
    )
    public Attribute.Double precip;
    
    
    @JAMSVarDescription(
        access = JAMSVarDescription.AccessType.READ,
        description = "Filling of the soil storage at beginning of model run",
        lowerBound = 0,
        upperBound = Double.POSITIVE_INFINITY,
        unit = "mm"
        )
        public Attribute.Double initSoilStor; ?//raus?
 
        
    @JAMSVarDescription(
        access = JAMSVarDescription.AccessType.READ,
        description = "Filling of the snow storage at beginning of model run",
        lowerBound = 0,
        upperBound = Double.POSITIVE_INFINITY,
        unit = "mm"
        )
        public Attribute.Double initSnowStor;  ?//raus?
    
       
    @JAMSVarDescription(
        access = JAMSVarDescription.AccessType.READ,
        description = "Filling of the depression storage at beginning of model run",
        lowerBound = 0,
        upperBound = Double.POSITIVE_INFINITY,
        unit = "mm"
        )
        public Attribute.Double initDepStor;  ?//raus?
    
    @JAMSVarDescription(
        access = JAMSVarDescription.AccessType.READ,
        description = "Saturation value of SoilStorage. If value is exceeded, the "
                        + "water flows into the depression storage",
        lowerBound = 0,
        upperBound = Double.POSITIVE_INFINITY,
        unit = "mm"
        )
        public Attribute.Double satValue;    !//muss in JAMs gesetzt werden!  ?raus?
     
    
    @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.WRITE,
            description = "Precipitation as snow",
            unit = "mm",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY
    )
    public Attribute.Double precip_snow;
 
    
    @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.WRITE,
            description = "Precipitation as rain",
            unit = "mm",
            lowerBound = 0,
            upperBound = Double.POSITIVE_INFINITY
    )
    public Attribute.Double precip_rain;
    
    
    @JAMSVarDescription(
        access = JAMSVarDescription.AccessType.WRITE,
        description = "Filling of the soil storage after time step",
        lowerBound = 0,
        upperBound = Double.POSITIVE_INFINITY,
        unit = "mm"
        )
        public Attribute.Double soilStor; ?//raus?
    
    
    @JAMSVarDescription(
        access = JAMSVarDescription.AccessType.WRITE,
        description = "Filling of the snow storage after time step",
        lowerBound = 0,
        upperBound = Double.POSITIVE_INFINITY,
        unit = "mm"
    )
    public Attribute.Double snowStor;  ?//raus?
    
    
    @JAMSVarDescription(
        access = JAMSVarDescription.AccessType.WRITE,
        description = "Filling of the depression storage after time step",
        lowerBound = 0,
        upperBound = Double.POSITIVE_INFINITY,
        unit = "mm"
        )
        public Attribute.Double depStor;  ?//raus?

    
    /*
     *  Component run stages
     */
    
    @Override
    public void init() {
        
        //Check if needed values are existing + set initial storages before first model run
        if (this.initSoilStor == null)   ?//raus?
            getModel().getRuntime().sendHalt(JAMS.i18n("parameter_Precipitation_initSoilStor_unspecified"));
        else
            soilStor.setValue(initSoilStor.getValue());
        
        if (this.initSnowStor == null)  ?//raus?
            getModel().getRuntime().sendHalt(JAMS.i18n("parameter_Precipitation_initSnowStor_unspecified"));
        else
            snowStor.setValue(initSnowStor.getValue());
        
        if (this.initDepStor == null)   ?//raus?
            getModel().getRuntime().sendHalt(JAMS.i18n("parameter_Precipitation_initDepStor_unspecified"));
        else
            depStor.setValue(initDepStor.getValue());
        
        if (this.satValue == null)  ?//raus?
            getModel().getRuntime().sendHalt(JAMS.i18n("parameter_Precipitation_satValue_unspecified"));
    }

    @Override
    public void run() {
        
        // if there are no values for a time step
        if (this.precip_rain == null)  ?//raus?
            getModel().getRuntime().sendHalt(JAMS.i18n("input_data_Precipitation_precip_rain_unspecified"));
        if (this.precip_snow == null)   ?//raus?
            getModel().getRuntime().sendHalt(JAMS.i18n("input_data_Precipitation_precip_snow_unspecified"));
        
        
        // Take given values
            
        double bT = this.baseTemp.getValue();
        double tT = this.transTemp.getValue();
        double minT = this.mintemp.getValue();
        double meanT = this.meantemp.getValue();
        double p = this.precip.getValue(); 
        double nightT = 0.5 * (meanT + minT); // Night temperature (mean of meanT and minT)
        double proportion;
        double snow_amount; 
        double rain_amount; 
        double soilStor = this.soilStor.getValue(); ?//raus?
        double snowStor = this.snowStor.getValue(); ?//raus?
        double depStor = this.depStor.getValue();   ?//raus?
        double snow = this.precip_snow.getValue();  ?//raus?! siehe Calculation snowStor
        double rain = this.precip_rain.getValue();  ?//raus?! siehe Calculation soilStor
        double sat = this.satValue.getValue(); ? //raus?
        
         
        if (p > 0) { //precipitation occurs
            if (nightT > (bT + tT)) { // night temperature too high for snowfall
            rain_amount = p;   
            snow_amount = 0;
            }
            else if (nightT < (bT - tT)){ // night temperature too low for rainfall
            rain_amount = 0;
            snow_amount = p;
            }
            else{
            proportion = (100/(tT * 2 )) * (nightT - bT) + 50; //water proportion in percentage oder: 25*nightT + 50
            rain_amount = p * (proportion/ 100); 
            snow_amount = p - rain_amount; 
            }
 
        } else { // no precipitation occurs
            rain_amount = 0;
            snow_amount = 0;
        }
     
        // Calculation of snowStor 
        snowStor = snowStor + snow_amount; //oder snowStor + snow ?  ??raus
        
        // Calculation of depStor and soilStor
        if (soilStor < sat){ //if soilStor is unsaturated
            soilStor = soilStor + rain_amount; //oder soilStor + rain
            if (soilStor >= sat){  ?ist dieser Schritt nötig? //if soilStor is saturated afterwards 
                depStor = depStor + (soilStor - sat); //excess water flows into depStor
                soilStor = sat; //soilStor gets saturation value
            }            
        }
        else { //if soilStor is already saturated 
            depStor = depStor + rain_amount;   //oder depStor + rain
            soilStor = sat;  //soilStor gets saturation value
        }
         
               
        //Write obtained values
        precip_rain.setValue(rain_amount);  
        precip_snow.setValue(snow_amount);
        this.soilStor.setValue(soilStor); ?//raus?
        this.snowStor.setValue(snowStor); ?//raus?
        this.depStor.setValue(depStor);   ?//raus?      
    }

    @Override
    public void cleanup() {
    }
}



?// woher weiß man, dass Bodenspeicher voll ist und Wasser in Muldenspeicher übergeht? (Sättigungswert?)
?// hat soilStor Kapazität : nimmt Regen + Schmelzwasser auf (abzüglich Verdunstung?)

 // Ueberpruefung der Speicherstaende nach Abflussereignissen und bei Bedarf
        // zuruecksetzen der Fuellstaende auf 0, falls Werte negativ sind
        if (soilStorage < 0) {
            soilStorage = 0;
        }
        if (depStorage < 0) {
            depStorage = 0;
        }


/