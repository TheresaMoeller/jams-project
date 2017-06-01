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
     
    /*
     *  Component run stages
     */
    
    @Override
    public void init() {
        if (this.initSoilStor == null)   ?//raus?
            getModel().getRuntime().sendHalt(JAMS.i18n("parameter_ABCModel_initStorage_unspecified"));
        else
            soilStor.setValue(initSoilStor.getValue());
    }

    @Override
    public void run() {
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
     
        //write obtained values
        precip_rain.setValue(rain_amount);  //weiter in Komponente Perkolation?
        precip_snow.setValue(snow_amount);
        this.soilStor.setValue(soilStor); ?//raus?
        
    }

    @Override
    public void cleanup() {
    }
}


?// SoilStor kann nicht berechnet werden, da Verdunstung abgezogen werden muss
?// woher weiß man, dass Bodenspeicher voll ist und Wasser in Muldenspeicher übergeht? (Sättigungswert?)
?// hat soilStor Kapazität : nimmt Regen + Schmelzwasser auf (abzüglich Verdunstung)

/