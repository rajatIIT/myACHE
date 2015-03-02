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
import com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListFilter;
/*
*
*/

public class TargetCBORRepository implements TargetRepository {

  private String location;
  private TargetModel targetModel;

  //RAJAT {
  public boolean multipleFlag=true;				// true : we want to write multiple pages info in one file
  private int multiplePagesBlockSize=5;		// to be retrieved from config file
  private File currentFile;
  private String currentFileLocation;
  private Target myTarget;
  private boolean writeWithCounter=true;
  private final static Logger LOGGER = Logger.getLogger(TargetCBORRepository.class.getName());
  int myCounter;
  //} RAJAT
  
  public TargetCBORRepository(){
	targetModel = new TargetModel("Kien Pham", "kien.pham@nyu.edu");//This contact information should be read from config file
	
	// RAJAT: multiplePagesBlockSize RETRIEVAL FROM CONFIG FILE
	LOGGER.info("rajat: initialize repository");
	myCounter=0;
	
	if (multipleFlag){
		// initialize the first file
		if (writeWithCounter)
			currentFileLocation = location + File.separator + 0 + "_" + 0 + ".cbor";
			else
			currentFileLocation = location + File.separator + 0 + ".cbor";
		try {
			(new File(currentFileLocation)).createNewFile();
			LOGGER.info("rajat: creating new file by name " + currentFileLocation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	}
  
  public TargetCBORRepository(String loc){
	  
	targetModel = new TargetModel("Kien Pham", "kien.pham@nyu.edu");//This contact information should be read from config file
	LOGGER.info("rajat: initialize repository");
	myCounter=0;
	
	  this.location = loc;
	  //RAJAT: multiplePagesBlockSize RETRIEVAL FROM CONFIG FILE
	   
	  if (multipleFlag) {

			// initialize the first file
			if (writeWithCounter)
				currentFileLocation = location + File.separator + 0 + "_" + 0 + ".cbor";
				else
				currentFileLocation = location + File.separator + 0 + ".cbor";
			try {
				(new File(currentFileLocation)).createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  
	  }
	  
  }

  /**
   * The method inserts a page with its respective crawl number.
   * 
   * On insert, the system should have already decided the file where to insert the CBOR Data for that page.
   * 
   * could be achieved by using a simple String parameter and update it whenever required.
   * 
   */
  public boolean insert(Target target, int counter) {
	  myCounter++;
	writeWithCounter=true;
	boolean contain = false;
	try {
    	myTarget=target;
   		manageFileWriting(multipleFlag,myCounter);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
	return contain;
  }
  

  public boolean insert(Target target) {
	  myCounter++;
	writeWithCounter=true;
    boolean contain = false;
    try {
    	myTarget=target;
   		manageFileWriting(multipleFlag,myCounter);
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
		
		mapper.writeValue(currentFile, this.targetModel);
			
	} else {
					// RAJAT {
		
//		if (writeWithCounter)
//			currentFile = new File(location + File.separator + URLEncoder.encode(url) + "_" + counter);
//			else
//			currentFile = new File(location + File.separator + URLEncoder.encode(url));
//		
//					String currentFilePath;
//					// writing file for the first time
//			    	if(currentFile.exists()){
//			    		if(writeWithCounter)
//			    		currentFilePath = location + File.separator + counter + "-" +  this.targetModel.timestamp + ".cbor";
//			    		else
//			    		currentFilePath = location + File.separator + "-" +  this.targetModel.timestamp + ".cbor";
//			    		
//			    	currentFile = new File(currentFilePath);
//			    	currentFile.createNewFile();
//			    	} else {
//			    	}
				
			    	// check if we have written pages more than file size
		LOGGER.info("rajat: counter val is " + counter);
					if( counter % multiplePagesBlockSize ==0){
						
			    		
			    		if(writeWithCounter)
			    			currentFileLocation = location + File.separator + counter + "-" +  this.targetModel.timestamp + ".cbor";
			    		else
			    			currentFileLocation = location + File.separator + this.targetModel.timestamp + ".cbor";
			    		
					new File(currentFileLocation).createNewFile();
					LOGGER.info("rajat: we completed one file ! Creating new file by name " + currentFileLocation);
					
			    	}
					
					/// manage writing using FileOutputStream
					
					// just open the file with currentfileLocation and write the respective data into that file
					
			    	// } RAJAT
					mapper.writeValue(new FileOutputStream(currentFileLocation, true),this.targetModel);
					LOGGER.info("rajat: Writing another page to file: " + currentFileLocation);
			    //	mapper.writeValue(currentFile, this.targetModel);
	}
	
}
  
}
