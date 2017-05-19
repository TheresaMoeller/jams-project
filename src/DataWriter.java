import jams.data.*;
import jams.model.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Theresa
 */
public class DataWriter extends JAMSComponent{
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
            description = "windspeed value read from file")
    public static Attribute.FileName outfile;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
            description = "Precip value read from file")
    public static Attribute.Double precip;
    
    @Override
    public void init() {  
        
        File file = new File(this.getModel().getWorkspaceDirectory(), outfile.getValue());
        
        //neue Datei erzeugen
        try{
            file.createNewFile();
        } catch (IOException ex){
            getModel().getRuntime().sendHalt("An error occured. Couldn't create file " + outfile.getValue() + ". Aborting.");
        }
        
        BufferedWriter bw = null;
        FileWriter fw = null;
        
        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            
            bw.write("# Results of JAMS Runoff Model");
            bw.newLine();
            
            for( Double k: output){
                bw.write(k.toString());
                bw.newLine();
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            bw.close();
            fw.close();
        } catch (IOException ex) {
            System.out.println("Could not close Writer");
        }
        
    }

    @Override
    public void run() {
    }

    @Override
    public void cleanup() {
    } 
    
}
