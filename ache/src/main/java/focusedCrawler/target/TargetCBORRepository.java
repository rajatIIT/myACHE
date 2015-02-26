package focusedCrawler.target;


import focusedCrawler.util.Target;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;

import weka.core.logging.Logger.Level;

import com.fasterxml.jackson.dataformat.cbor.*;
import com.fasterxml.jackson.databind.ObjectMapper;
/*
*
*/

public class TargetCBORRepository implements TargetRepository {

  private String location;
  private TargetModel targetModel;

  //RAJAT {
  public boolean multipleFlag=true;				// true : we want to write multiple pages info in one file
  private int multiplePagesBlockSize;		// to be retrieved from config file
  private File currentFile;
  private Target myTarget;
  private boolean writeWithCounter=true;
  private File logFile;
  private PrintWriter logWriter;
  //} RAJAT
  
  public TargetCBORRepository(){
	targetModel = new TargetModel("Kien Pham", "kien.pham@nyu.edu");//This contact information should be read from config file
	// RAJAT: multiplePagesBlockSize RETRIEVAL FROM CONFIG FILE
	
	//{RAJAT
	try {
		createLogFile();
		writeToLog("Creating log file");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//}RAJAT
	
	}
  
  public TargetCBORRepository(String loc){
	targetModel = new TargetModel("Kien Pham", "kien.pham@nyu.edu");//This contact information should be read from config file
	  this.location = loc;
	  //RAJAT: multiplePagesBlockSize RETRIEVAL FROM CONFIG FILE
	  
	//{RAJAT
		try {
			createLogFile();
			writeToLog("Creating log file");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//}RAJAT
	  
  }

  /**
   * The method inserts a page with its respective crawl number.
   */
  public boolean insert(Target target, int counter) {
	writeWithCounter=true;
	boolean contain = false;
	try {
    	myTarget=target;
   		manageFileWriting(multipleFlag,counter);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
	return contain;
  }
  

  public boolean insert(Target target) {
	writeWithCounter=false;
    boolean contain = false;
    try {
    	myTarget=target;
   		manageFileWriting(multipleFlag,0);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    return contain;
  }
  
//{RAJAT
  public String getLocation(){
	  return location;
  }

public boolean getMultipleFlag() {
	return multipleFlag;
}

public void setMultipleFlag(boolean multipleFlag) {
	this.multipleFlag = multipleFlag;
}


private void createLogFile() throws IOException{
	logFile = new File(location + File.separator + "rajatlog");
	logFile.createNewFile();
	
}


private void writeToLog(String inputMessage){
	try {
		logWriter= new PrintWriter(logFile);
		logWriter.println(inputMessage);
		logWriter.close();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
//}RAJAT

/**
 * Manages writing to CBOR files depending on storage scheme (Domain Name or counter based)
 * 
 * 
 * @param inputFlag true if counter based scheme is used
 * @param counter number of pages crawled
 * @throws IOException
 */
private void manageFileWriting(boolean inputFlag, int counter) throws IOException{

	URL urlObj = new URL(myTarget.getIdentifier());
	String host = urlObj.getHost();
	String url = myTarget.getIdentifier();

	this.targetModel.setTimestamp();
	this.targetModel.setUrl(url);
	this.targetModel.setContent(myTarget.getSource());
	
	CBORFactory f = new CBORFactory();
	ObjectMapper mapper = new ObjectMapper(f);
	if (!inputFlag) {
		File dir = new File(location + File.separator + URLEncoder.encode(host));
		if(!dir.exists()){
            dir.mkdir();
        }
		
		if (writeWithCounter)
		currentFile = new File(dir.toString() + File.separator + URLEncoder.encode(url) + "_" + counter);
		else
		currentFile = new File(dir.toString() + File.separator + URLEncoder.encode(url));
			
	} else {
					// RAJAT {
					String currentFilePath;
					// writing file for the first time
			    	if(!currentFile.exists()){
			    		writeToLog("First file does not exists");
			    		
			    		if(writeWithCounter)
			    		currentFilePath = location + File.separator + counter + "-" +  this.targetModel.timestamp + ".cbor";
			    		else
			    		currentFilePath = location + File.separator + "-" +  this.targetModel.timestamp + ".cbor";
			    		
			    	writeToLog("writing file with name" + currentFilePath);
			    	
			    	writeToLog("Creating New File");
			    	currentFile = new File(currentFilePath);
			    	currentFile.createNewFile();
			    	} else {
			    		writeToLog("First file exists");
			    	}
				
			    	// check if we have written pages more than file size
			    	if(counter%multiplePagesBlockSize==0){
			    		
			    		if(writeWithCounter)
			    			currentFilePath = location + File.separator + counter + "-" +  this.targetModel.timestamp + ".cbor";
			    		else
			    			currentFilePath = location + File.separator + this.targetModel.timestamp + ".cbor";
			    		
					currentFile = new File(currentFilePath);
					currentFile.createNewFile();
					writeToLog("writing file with name" + currentFilePath);
			    	}
					
			    	// } RAJAT
			    	
	}
	mapper.writeValue(currentFile, this.targetModel);
}
  
}
