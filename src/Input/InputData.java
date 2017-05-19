package Input;

import jams.data.*;
import jams.model.*;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Theresa
 */
public class InputData {

    private String date;
    
    private List<Double> values = new ArrayList<Double>();

//    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
//            description = "Precip value read from file")
//    private Attribute.Double precip;
//
//    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
//            description = "Minumum Temperature value read from file")
//    private Attribute.Double mintemp;
//
//    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
//            description = "Mean Temperature value read from file")
//    private Attribute.Double meantemp;
//
//    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
//            description = "Maximum Temperature value read from file")
//    private Attribute.Double maxtemp;
//
//    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
//            description = "Sunshine hours read from file")
//    private Attribute.Double sunshine;
//
//    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
//            description = "Relative Humidity value read from file")
//    private Attribute.Double relhum;
//
//    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
//            description = "Absolute Humidity value read from file")
//    private Attribute.Double abshum;
//
//    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
//            description = "windspeed value read from file")
//    private Attribute.Double windspeed;

    public InputData() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    
    public List<Double> getValues(){
        return values;
    }
    
    public void setValues(List<Double> values){
        this.values = values;
    }

//    public Attribute.Double getPrecip() {
//        return precip;
//    }
//
//    public void setPrecip(Attribute.Double precip) {
//        this.precip = precip;
//    }
//
//    public Attribute.Double getMintemp() {
//        return mintemp;
//    }
//
//    public void setMintemp(Attribute.Double mintemp) {
//        this.mintemp = mintemp;
//    }
//
//    public Attribute.Double getMeantemp() {
//        return meantemp;
//    }
//
//    public void setMeantemp(Attribute.Double meantemp) {
//        this.meantemp = meantemp;
//    }
//
//    public Attribute.Double getMaxtemp() {
//        return maxtemp;
//    }
//
//    public void setMaxtemp(Attribute.Double maxtemp) {
//        this.maxtemp = maxtemp;
//    }
//
//    public Attribute.Double getSunshine() {
//        return sunshine;
//    }
//
//    public void setSunshine(Attribute.Double sunshine) {
//        this.sunshine = sunshine;
//    }
//
//    public Attribute.Double getRelhum() {
//        return relhum;
//    }
//
//    public void setRelhum(Attribute.Double relhum) {
//        this.relhum = relhum;
//    }
//
//    public Attribute.Double getAbshum() {
//        return abshum;
//    }
//
//    public void setAbshum(Attribute.Double abshum) {
//        this.abshum = abshum;
//    }
//
//    public Attribute.Double getWindspeed() {
//        return windspeed;
//    }
//
//    public void setWindspeed(Attribute.Double windspeed) {
//        this.windspeed = windspeed;
//    }
    
    

}
