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
        description = "Saturation value of SoilStorage. If value is exceeded, "
                       + "the water flows into the depression storage",
        lowerBound = 0,
        upperBound = Double.POSITIVE_INFINITY,
        unit = "mm"
    )
    public Attribute.Double satValue;  
     
    
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
        description = "Filling of the snow storage after time step",
        lowerBound = 0,
        upperBound = Double.POSITIVE_INFINITY,
        unit = "mm"
    )
    public Attribute.Double snowStor;  
   
    
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
    

    /*
     *  Component run stages
     */
    
    @Override
    public void init() {
    }

    @Override
    public void run() {
                
        double nighttemp = 0.5 * (meantemp.getValue() + mintemp.getValue()); // Night temperature (mean of meantemp and mintemp)
        double proportion;
        double snow_amount; 
        double rain_amount; 
               
         
        if (precip.getValue() > 0) { //precipitation occurs
            if (nighttemp > (baseTemp.getValue() + transTemp.getValue())) { // night temperature too high for snowfall
            rain_amount = precip.getValue();   
            snow_amount = 0;
            }
            else if (nighttemp < (baseTemp.getValue() - transTemp.getValue())){ // night temperature too low for rainfall
            rain_amount = 0;
            snow_amount = precip.getValue();
            }
            else{
            proportion = (100/(transTemp.getValue() * 2 )) * (nighttemp - baseTemp.getValue()) + 50; //water proportion in percentage 
            rain_amount = precip.getValue() * (proportion/ 100); 
            snow_amount = precip.getValue() - rain_amount; 
            }
 
        } else { // no precipitation occurs
            rain_amount = 0;
            snow_amount = 0;
        }
     
        // Calculation of snowStor 
        snowStor.setValue(snowStor.getValue() + snow_amount); 
        
        // Calculation of depStor and soilStor
        if (soilStor.getValue() < satValue.getValue()){ //if soilStor is unsaturated
            soilStor.setValue(soilStor.getValue() + rain_amount); 
            if (soilStor.getValue() >= satValue.getValue()){  //if soilStor is saturated afterwards 
                depStor.setValue(depStor.getValue() + (soilStor.getValue() - satValue.getValue())); //excess water flows into depStor
                soilStor.setValue(satValue.getValue()); //soilStor gets saturation value
            }            
        }
        else { //if soilStor is already saturated 
            depStor.setValue(depStor.getValue() + rain_amount);   
            soilStor.setValue(satValue.getValue());  //soilStor gets saturation value
        }
                       
        //Write obtained values
        precip_rain.setValue(rain_amount);  
        precip_snow.setValue(snow_amount);
         
    }

    @Override
    public void cleanup() {
    }
}