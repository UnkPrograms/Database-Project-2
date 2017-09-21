/**
 * Name: Ryunki Song
 * Class: CS460 Database
 * Professor: McCann
 * TA: Jacob, Yawen
 * Due Date: 1/25/2017 3:30 PM
 * Project 1A
 * 
 * Description: Prog1A.java takes any type of CSV file and converts the contents into a 
 * .bin file. For the given 23 fields specific to the taxi csv files, this class
 * ensures that each column/field takes up the same amount of bytes/memory.
 * This means each line of data takes up the same amount of memory when converted into a 
 * .bin file. 
 * 
 * How the program was written: First there is a basic class called MyDataRecord which
 * holds each line of data in the csv file which gets its data from an ArrayList in the Prog1A class.
 * An ArrayList is used so that we don't have to re-read the csv file and instead 
 * can access the data which has been sanitized in the ArrayList.
 * The Prog1A class itself is broken into 3 stages.
 * Stage 1: First read and sanitize the input data from the csv file and insert it into the ArrayList
 * Stage 2: Grab each line from the ArrayList and make each field equal size in terms of byte size
 * 			while placing the data in MyDataRecord. Then use these data records to write each line of 
 * 			data into a .bin file
 * Stage 3: Read back the data records and print them out to check for bugs and to confirm the written
 *			.bin file has the correct data
 * 
 * 
 * NOTE: # represent filler spaces for string related fields since in order for string
 * fields to take up the same amount of memory, there needs to be a "dummy" character
 * to fill in the spaces if one string is shorter than the other. 
 * 
 * Programming Language: Java JDK 1.8
 * 
 * HOW TO RUN: Compile the this java class with the given makefile (command: "make all").
 * Then type the command "java Prog1A file.csv" (REMEMBER to add the extension on the csv file)
 * Then there will be statements that are printed to let you know the process worked (un)/successfully
 * NOTE: All files should be within the same directory
 */


import java.io.*;
import java.util.ArrayList;

public class Prog1A {
	
	public static void main (String [] args){
		
		/** START Stage 1: Sanitizing the input **/
		ArrayList<String> dataList = new ArrayList<String>();			 		//Where the sanitized line inputs are stored
		ArrayList<MyDataRecord> myRecordList = new ArrayList<MyDataRecord>();	//Where the record data are stored
		
		// First declare/initialize variables to get longest lengths of the String fields
		int maxTripIDLen=0;
		int maxTaxiIDLen=0;
		int maxTripStartLen=0;
		int maxTripEndLen=0;
		int maxTripTotalLen=0;
		int maxPayTypeLen=0;
		int maxCompLen=0;
		int maxPickLocLen=0;
		int maxDropLocLen=0;
		
		
		
		
//		/* NOW ERROR CHECK THE USER INPUT */
		//Check if we have appropriate number of inputs
		if(args.length > 1 || args.length < 1){
			System.err.println("Please input the proper number of parameters - there should be 1 parameter");				
			System.exit(-1);
		}

		String csvFile = args[0];		//Get csv file name from command line argument
		String name = csvFile.substring(csvFile.length()-3, csvFile.length());
		
		if(name.compareTo("csv") != 0);
//			System.err.println("Use a .csv file");
		
		/* END ERROR CHECKING THE USER INPUT */
		
		
		String line = "";				//String var to contain the line of data read in
        String csvSplitBy = ",";		//Use commas to split the line/fields into an array
//        String csvFile = "chicagotaxi-nov2016.csv";
        
        System.out.println("Running...");
//        System.out.println("File name is: "+args[0]);	//Message for the user

        //Now read the file and begin sanitizing
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        	
        	//Check for an empty file
        	if (br.readLine() == null) {
        	    System.out.println("File is Empty");
        	    br.close();
        	}
        	else{
    		/*
    		 * Notice if file is not empty, we just read the field names, so now we 
    		 * while loop to setup each line to be the proper format.
    		 */
        		while ((line = br.readLine()) != null) {
        			char[] chArray = line.toCharArray();	
        			String lineToUse = "";							//This is what will be used to insert into the ArrayList called DataList
        		
        			//Check for consecutive commas, add the default '#' character to fill space between commas. This ensures the split method works correctly
        			for(int i=0; i<chArray.length-1; i++){
        				if(chArray[i]==',' && chArray[i+1]==','){	
        					lineToUse += chArray[i];
        					lineToUse += '#';
        				}
        				//Remove and sanitize these characters by not adding them to the lineToUse var
        				else if(chArray[i]==' ' || chArray[i]=='/' || chArray[i]==':' || chArray[i]=='$')
        					continue;
        				//Otherwise just add the character to the lineToUse
        				else
        					lineToUse += chArray[i];
        			}
        			//The for loops stops one step before the last element in the array so add the last element to the string
        			lineToUse += chArray[chArray.length-1];	
        		
        			//If last character is a comma, then the last field is empty for the line so add default '#'
        			if(chArray[chArray.length-1] == ',')
        				lineToUse += '#';
 
        			//At this point, lineToUse has no empty fields and has commas between each field. 
        			
        			dataList.add(lineToUse);						//Add the data to the ArrayList - the ArrayList is used later

        			String[] info = lineToUse.split(csvSplitBy);	// use comma as separator        			
        			
        			//There are cases where commas appear where they are not used to split the fields (ie. a company name: "Ryan's Supper, Lunch Bistro")
        			//where the comma is not meant to denote a different field value of "Lunch Bistro". This is why I used an index variable so that 
        			//when this does occur, it can be taken care of. If each index was hard coded, such as info[0], then Lunch Bistro would be treated
        			//as a new field which is NOT what we want.
        			//NOTE: Each i++ means we move onto the next field. i+=12 means we move 12 fields to the right
        			int i = 0;
        			if(info[i].length() > maxTripIDLen)  			//Check string length of the trip ID                    
        				maxTripIDLen = info[i].length(); 	i++;
        			if(info[i].length() > maxTaxiIDLen)	 			//Check string length of the taxi ID
        				maxTaxiIDLen = info[i].length(); 	i++;
        			if(info[i].length() > maxTripStartLen) 			//Check string length of the trip start
        				maxTripStartLen = info[i].length(); i++;
        			if(info[i].length() > maxTripEndLen)   			//Check string length of the trip end
        				maxTripEndLen = info[i].length();	i+=11;	//Next typical string field is at index 14 so 3+11 (add 11 to get to the next String field)
           			if(info[i].length() > maxTripTotalLen)   		//Check string length of the trip end
           				maxTripTotalLen = info[i].length();	i++;	
        			if(info[i].length() > maxPayTypeLen)    		//Check string length of the pay type
        				maxPayTypeLen = info[i].length();	i++;
        				
        			//This is where we check for the case of extraneous commas in the company's name
        			//Basically if the next field contains an integer or the default '#' character, 
        			//then we know it is not a company name and instead the field we expect it to be, which is a double (Pickup Latitude Field).
                	char[] check = info[i+1].toCharArray();
                	if(check[check.length-1] == '1' || check[check.length-1] == '2' || check[check.length-1] == '3' || 
                			check[check.length-1] == '4' || check[check.length-1] == '5' || check[check.length-1] == '6' || 
                			check[check.length-1] == '7' || check[check.length-1] == '8' || check[check.length-1] == '9' || 
                			check[check.length-1] == '#'){
            			if(info[i].length() > maxCompLen)
            				maxCompLen = info[i].length();
            			i += 3;											//Here you are at index 16 + 3 = 19
            		}
                	//Else it contains a string so we know that it needs to be appended with the first half of the company's names
            		else{
            			//Check if the max length needs to be updated because this is a string length that was unchecked before
            			if((info[i]+info[i+1]).length() > maxCompLen)
            				maxCompLen = (info[i]+info[i+1]).length();	//Update max length 
            			i++;											//Increment so you're at the second field of company name (index 17)
            			i+=3;   										//Adjusted index for Pickup Location is (index 20)
            		}
            		if(info[i].length() > maxPickLocLen)    			//Check string length of the pickup location
        				maxPickLocLen = info[i].length();	i+=3;  		//Next typical string field is at index 16
        			if(info[i].length() > maxDropLocLen)    			//Check string length of the dropoff location
        				maxDropLocLen = info[i].length();	
        		}
        	}

        } catch (IOException e) {
            System.out.println("Unable to sanitize input from CSV file.");
            System.exit(-1);
        }
        /** END Stage 1: Sanitizing the input **/
        
        
		/** START Stage 2: Set all the fields in MyDataRecord class for every line of data in the dataList arrayList and write to .bin file**/
        File             fileRef;       				      								// used to create the file
        RandomAccessFile dataStream = null;  			 	  								// specializes the file I/O
        MyDataRecord     myDataRecord;      			      								// the objects to write/read
        double           numberOfRecords = 0; 				  								// loop counter for reading file
        String 			 convertFileName = args[0].substring(0, args[0].length()-3)+"bin";  // changed file.csv to file.bin
//        fileRef = new File ("chicagotaxi-nov2016.bin");	  	  
        fileRef = new File (convertFileName);	  	  
//
//        
        System.out.println("File converted binary file name is: "+convertFileName);			// message for the user
        
    	try {	
    		dataStream = new RandomAccessFile(fileRef,"rw");
    	} catch (IOException e) {
    		System.out.println("I/O ERROR: Something went wrong with the "
                         + "creation of the RandomAccessFile object.");
    		System.exit(-1);
    	}
        
    	//We need to calculate the total bytes used in a string NOTE: character = 1 byte
    	int maxStringBytesTotal = maxTripIDLen+maxTaxiIDLen+maxTripStartLen+maxTripEndLen+maxTripTotalLen+maxPayTypeLen+maxCompLen+maxPickLocLen+maxDropLocLen;
    	
    	/*
    	 * Summary of the for loop: For each MyDataRecord object, make each respective field the appropriate length.
    	 * NOTE: Different fields may have different lengths, but data within the same fields consume the same amount of memory.
    	 *  
    	 * Then grab each line of data from the ArrayList and split the fields by commas. NOTE: These lines have been sanitized, 
    	 * so each field is not empty because empty fields contain the default '#' character for example. 
    	 * 
    	 * Parse the line using arrays and insert them into the proper record fields in the MyDataRecord object.
    	 * 
    	 * Then write the data/records into the .bin file.
    	 */
    	int count = 0;
        for(String record : dataList){
        	count++;
        	
            myDataRecord = new MyDataRecord();						//Create the object to read/write
  
        	//Now set the proper bytes size for the string related setters in the read/write object
        	myDataRecord.setTripIDLength(maxTripIDLen);				//Set the max bytes for every Trip ID field    	
        	myDataRecord.setTaxiIDLength(maxTaxiIDLen);  			//Set the max bytes for every Taxi ID field    	
        	myDataRecord.setTripStartLength(maxTripStartLen);		//Set the max bytes for every Trip Start field    	
        	myDataRecord.setTripEndLength(maxTripEndLen);  			//Set the max bytes for every Trip End field    	
        	myDataRecord.setTripTotalLength(maxTripTotalLen);		//Set the max bytes for every Trip Total field    	
        	myDataRecord.setPaymentTypeLength(maxPayTypeLen);  		//Set the max bytes for every Pay Type field
        	myDataRecord.setCompanyLength(maxCompLen);  			//Set the max bytes for every Company field
        	myDataRecord.setPickupCenLocLength(maxPickLocLen);  	//Set the max bytes for every Pickup Location field
        	myDataRecord.setDropOffCenLocLength(maxDropLocLen);  	//Set the max bytes for every DropOff Location field
        	
        	myDataRecord.setRecordLength(maxStringBytesTotal);		//Set the length of bytes for every record and line of data
        	
        	if(myDataRecord.RECORD_LENGTH > numberOfRecords)
        		numberOfRecords = myDataRecord.RECORD_LENGTH;
//        	try {
//				System.out.println(dataStream.length()+" "+numberOfRecords+" "+maxStringBytesTotal);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
        	
        	String[] info = record.split(csvSplitBy);				//Split the line by commas so that each element in the array represents a field
        	
        	//Now set the proper data fields in the read/write object
        	//NOTE: Insert -1 for fields that are identified as a integer/double field and was an empty field "denoted by a single '#' character
        	//NOTE: For string related fields, we can just simply pass in the info array parameter because the method already handles the resizing for Strings
        	//This idea of using an index variable instead of hard coded array indexes is the same as before. Using an index variable allows for handling cases dynamically
        	//since there may be extraneous inputs (particularly those that are comma related such as company names)
        	int i=0;
        	myDataRecord.setTripID(info[i]);	i++;						
        	myDataRecord.setTaxiID(info[i]);	i++;
        	myDataRecord.setTripStart(info[i]);	i++;					
        	myDataRecord.setTripEnd(info[i]);	i++;

        	if(info[i].equals("#"))										
        		myDataRecord.setTripSeconds(-1); 
        	else myDataRecord.setTripSeconds(Integer.parseInt(info[i]));
        	i++;
        	
        	if(info[i].equals("#"))
        		myDataRecord.setTripMiles(-1);
        	else myDataRecord.setTripMiles(Double.parseDouble(info[i]));
        	i++;
        	
        	if(info[i].equals("#"))
        		myDataRecord.setPickupCenTract(-1);
        	else myDataRecord.setPickupCenTract(Double.parseDouble(info[i]));
        	i++;
        	
        	if(info[i].equals("#"))
        		myDataRecord.setDropoffCenTract(-1);
        	else myDataRecord.setDropoffCenTract(Double.parseDouble(info[i]));
        	i++;
        	
        	if(info[i].equals("#"))
        		myDataRecord.setPickupCommArea(-1);
        	else myDataRecord.setPickupCommArea(Integer.parseInt(info[i]));
        	i++;
        	
        	if(info[i].equals("#"))
        		myDataRecord.setDropOffCommArea(-1);
        	else myDataRecord.setDropOffCommArea(Integer.parseInt(info[i]));
        	i++;
        	
        	if(info[i].equals("#"))
        		myDataRecord.setFare(-1);
        	else myDataRecord.setFare(Double.parseDouble(info[i]));
        	i++;
        	
        	if(info[i].equals("#"))
        		myDataRecord.setTips(-1);
        	else myDataRecord.setTips(Double.parseDouble(info[i]));
        	i++;
        	
        	if(info[i].equals("#"))
        		myDataRecord.setTolls(-1);
        	else myDataRecord.setTolls(Double.parseDouble(info[i]));
        	i++;
        	
        	if(info[i].equals("#"))
        		myDataRecord.setExtras(-1);
        	else myDataRecord.setExtras(Double.parseDouble(info[i]));
        	i++;
        	
        	myDataRecord.setTripTotal(info[i]);		i++;
        	myDataRecord.setPaymentType(info[i]);	i++;
        	myDataRecord.setCompany(info[i]); 		i++;
        	
        	//Here we need to check if the company names have commas in them since it will distort how the fields are separated in the array
        	//Basically since this field is typically a double (Pickup Latitude) this is where we check if the company name had a comma within it
        	if(info[i].equals("#")){
        		myDataRecord.setPickupCenLat(-1);
        		i++;
        	}
        	else{ 
        		// Check if the last character is a digit.
        		// If so, then it is a normal field and we are done
        		char[] check = info[i].toCharArray();
        		if(check[check.length-1] == '1' || check[check.length-1] == '2' || check[check.length-1] == '3' || 
        				check[check.length-1] == '4' || check[check.length-1] == '5' || check[check.length-1] == '6' || 
        				check[check.length-1] == '7' || check[check.length-1] == '8' || check[check.length-1] == '9'){
        			myDataRecord.setPickupCenLat(Double.parseDouble(info[i]));        
        			i++;
        		}
        		//Else, then the company name has a comma so we need to adjust the field
        		else{
        			myDataRecord.setCompany((info[i-1].substring(1, info[i-1].length())+','+info[i].substring(0, info[i].length()-1)));				//Get the full company name and set into data record
        			i++;														//Increment so that we are at the Pickup Latitude field
        			myDataRecord.setPickupCenLat(Double.parseDouble(info[i]));        
        			i++;														//Move on to the next field as if nothing happened
        		}
        	}
        	
        	if(info[i].equals("#"))
        		myDataRecord.setPickupCenLon(-1);
        	else myDataRecord.setPickupCenLon(Double.parseDouble(info[i]));
        	i++;
        	
        	myDataRecord.setPickupCenLoc(info[i]);	i++;
        	
        	if(info[i].equals("#"))
        		myDataRecord.setDropOffCenLat(-1);
        	else myDataRecord.setDropOffCenLat(Double.parseDouble(info[i]));
        	i++;
        	
        	if(info[i].equals("#"))
        		myDataRecord.setDropOffCenLon(-1);
        	else myDataRecord.setDropOffCenLon(Double.parseDouble(info[i]));
        	i++;
        	
        	myDataRecord.setDropOffCenLoc(info[i]);
        	
        	
        	myDataRecord.dumpObject(dataStream);
        	myRecordList.add(myDataRecord);
        	

        	
    		//Now dump the information into the binary file
        	if(count == dataList.size()){
        		int lengthBetweenIDandTotal = maxTaxiIDLen+maxTripStartLen+maxTripEndLen+(4*3)+(8*7); //3 ints, 7 double fields and 3 string fields in between -- remember we are doing byte size so 3 int means 4*3 
        		myDataRecord.dumpInfo(dataStream, (int)numberOfRecords, maxTripIDLen, lengthBetweenIDandTotal, maxTripTotalLen);
        	}
        		
        }
        /** END Stage 2: Set all the fields in MyDataRecord class for every line of data in the dataList arrayList and write to .bin file**/
        

        
        /** START STAGE 3: Now read back the Data Records placed into the .bin file **/
		/*
		 * Move the file pointer (which marks the byte with which the next
		 * access will begin) to the front of the file (that is, to byte 0).
		 */
		try {
			dataStream.seek(0);
		} catch (IOException e) {
			System.out.println("I/O ERROR: Seems we can't reset the file " + "pointer to the start of the file.");
			System.exit(-1);
		}

		/*
		 * Count number of records, unless it is zero, then we know the file is empty or only contains the field names
		 */

		System.out.println("The length of each record is " + (int)numberOfRecords);
		System.out.println("The length of each TripID is " + maxTripIDLen);
		int lengthBetweenIDandTotal = maxTaxiIDLen+maxTripStartLen+maxTripEndLen+(4*3)+(8*7); //3 ints, 7 double fields and 3 string fields in between -- remember we are doing byte size so 3 int means 4*3 
		System.out.println("The length of each TripTotal is " +maxTripTotalLen);
		System.out.println("The length between TripID and TripTotal is " + lengthBetweenIDandTotal);
		
		if(numberOfRecords !=0 ){
			try {
				numberOfRecords = (double)(dataStream.length()-16) / (double)(numberOfRecords);
			} catch (IOException e) {
				System.out.println("I/O ERROR: Couldn't get the file's length.");
				System.exit(-1);
				return;
			}
		}
		else
			System.out.println("The CSV file is EMPTY!"); //Message for the user

		System.out.println("There are " + (int)numberOfRecords + " records in the file.\n");
//		System.out.println("There are: "+myRecordList.size()+" records in the file");
		
//		//The commented out block was used for my own console debugging purposes
//		for (MyDataRecord myRecord : myRecordList) {
//			myRecord.fetchObject(dataStream);
//			System.out.println(myRecord.getPickupCenLon());
//			System.out.println(myRecord.getTripID() + "," + myRecord.getTaxiID() + myRecord.getTripStart()
//					+ myRecord.getTripEnd() + myRecord.getTripSeconds() + myRecord.getTripMiles()
//					+ myRecord.getPickupCenTract() + myRecord.getDropoffCenTract() + myRecord.getPickupCommArea()
//					+ myRecord.getDropOffCommArea() + myRecord.getFare() + myRecord.getTips() + myRecord.getTolls()
//					+ myRecord.getExtras() + myRecord.getTripTotal() + myRecord.getPaymentType() + myRecord.getCompany()
//					+ myRecord.getPickupCenLat() + myRecord.getPickupCenLon() + myRecord.getPickupCenLoc()
//					+ myRecord.getDropOffCenLat() + myRecord.getDropOffCenLon() + myRecord.getDropOffCenLoc());
//			System.out.println();
//		}
		
		// Clean-up by closing the file
		try {
			dataStream.close();
			System.out.println("Success!");		//message for the user
		} catch (IOException e) {
			System.out.println("VERY STRANGE I/O ERROR: Couldn't close " + "the file!");
		}

		/** START STAGE 3: Now read back the Data Records placed into the .bin file **/
	}
}

/**
 * Class name: MyDataRecord
 * Imports used: java.io.*
 * 
 * The purpose of this class is hold the 23 fields for each line of data which then can be 
 * written into a .bin file. This class also has a method that can fetch and dump objects which
 * return and store the lines of data stored such as their field values and the amount of bytes
 * each field consumes.
 * 
 * @author Ryunki Song
 *
 */
class MyDataRecord{
	
	private int RECORD_LENGTH_NO_STRING = 100;  //11 doubles + 3 ints fields = 11(8) + 3(4) DOES NOT INCLUDE STRINGS!
	public int RECORD_LENGTH;					//Including STRINGS, the total bytes consumed for every line of data
	
	//String related fields
	private int TRIP_ID_LENGTH, TAXI_ID_LENGTH, TRIP_START_LENGTH, TRIP_END_LENGTH, TRIP_TOTAL_LENGTH, PAYMENT_TYPE_LENGTH, COMPANY_LENGTH, PICKUP_CEN_LOC_LENGTH, DROPOFF_CEN_LOC_LENGTH;	
	private String tripID, taxiID, tripStart, tripEnd, tripTotal, paymentType, company, pickupCenLoc, dropOffCenLoc;

	//Double related fields
	private int  DOUBLE_BYTE_SIZE = 8;
	private double tripMiles, pickupCenTract, dropOffCenTract, fare, tips, tolls, extras, pickupCenLat, pickupCenLon, dropOffCenLat, dropOffCenLon;

	//int related fields
	private int INTEGER_BYTE_SIZE = 4;
	private int tripSeconds, pickupCommArea, dropOffCommArea;
	
	/*START Getters for data fields*/
	public int 	  getRecordLength() 	{ return(RECORD_LENGTH); }
	public int 	  getTripIDLength() 	{ return(this.TRIP_ID_LENGTH); }
	public String getTripID() 			{ return(tripID); }
	public int 	  getTaxiIDLength() 	{ return(this.TAXI_ID_LENGTH); }
	public String getTaxiID() 			{ return(taxiID); }
	public int 	  getTripStartLength() 	{ return(this.TRIP_START_LENGTH); }
	public String getTripStart() 		{ return this.tripStart; }
	public int 	  getTripEndLength() 	{ return(this.TRIP_END_LENGTH); }
	public String getTripEnd() 			{ return this.tripEnd; }
	public int 	  getTripSeconds() 		{ return this.tripSeconds; }
	public double getTripMiles() 		{ return this.tripMiles; }
	public double getPickupCenTract()	{ return this.pickupCenTract; }
	public double getDropoffCenTract()	{ return this.dropOffCenTract; }
	public int 	  getPickupCommArea() 	{ return this.pickupCommArea; }
	public int	  getDropOffCommArea() 	{ return this.dropOffCommArea; }
	public double getFare() 			{ return this.fare; }
	public double getTips() 			{ return this.tips; }
	public double getTolls() 			{ return this.tolls; }
	public double getExtras() 			{ return this.extras; }
	public int 	  getTripTotalLength() 	{ return(this.TRIP_TOTAL_LENGTH); }
	public String getTripTotal() 		{ return this.tripTotal; }
	public int    getPaymentTypeLength() { return this.PAYMENT_TYPE_LENGTH; }
	public String getPaymentType() 		{ return this.paymentType; }
	public int    getCompanyLength() 	{ return this.COMPANY_LENGTH; }
	public String getCompany() 			{ return this.company; }
	public double getPickupCenLat() 	{ return this.pickupCenLat; }
	public double getPickupCenLon() 	{ return this.pickupCenLon; }
	public int	  getPickupCenLocLength() { return this.PICKUP_CEN_LOC_LENGTH; }
	public String getPickupCenLoc() 	{ return this.pickupCenLoc; }
	public double getDropOffCenLat() 	{ return this.dropOffCenLat; }
	public double getDropOffCenLon() 	{ return this.dropOffCenLon; }
	public int    getDropOffCenLocLength() { return this.DROPOFF_CEN_LOC_LENGTH; }
	public String getDropOffCenLoc() 	{ return this.dropOffCenLoc; }	
	/*END Getters for data fields*/
	
	/*START Setters for data fields*/
	public void setRecordLength(int bytes) { this.RECORD_LENGTH = this.RECORD_LENGTH_NO_STRING + bytes; }      //bytes = 100 + all string related bytes length

	public void setTripIDLength(int tripIDLen) { this.TRIP_ID_LENGTH = tripIDLen; }
	public void setTripID(String tripID) { 
		this.tripID = tripID; 
		while(this.tripID.length() < TRIP_ID_LENGTH)
			this.tripID += "#";							// '#' represents the default padding value
	}
	
	public void setTaxiIDLength(int taxiIDLen) { this.TAXI_ID_LENGTH = taxiIDLen; }
	public void setTaxiID(String taxiID) { 
		this.taxiID = taxiID; 
		while(this.taxiID.length() < TAXI_ID_LENGTH)
			this.taxiID += "#";							// '#' represents the default padding value
	}
	
	public void setTripStartLength(int tripStartLen) { this.TRIP_START_LENGTH = tripStartLen; }
	public void setTripStart(String tripStart) { 
		this.tripStart = tripStart; 
		while(this.tripStart.length() < TRIP_START_LENGTH)
			this.tripStart += "#";						// '#' represents the default padding value
	}
	
	public void setTripEndLength(int tripEndLen) { this.TRIP_END_LENGTH = tripEndLen; }
	public void setTripEnd(String tripEnd) { 
		this.tripEnd = tripEnd; 
		while(this.tripEnd.length() < TRIP_END_LENGTH)
			this.tripEnd+= "#";							// '#' represents the default padding value
	}
	
	public void setTripSeconds(int tripSeconds) { this.tripSeconds = tripSeconds; }
	public void setTripMiles(double tripMiles) { this.tripMiles = tripMiles; }
	public void setPickupCenTract(double pickupCenTract){ this.pickupCenTract = pickupCenTract; }
	public void setDropoffCenTract(double dropOffCenTract){ this.dropOffCenTract = dropOffCenTract; }
	public void setPickupCommArea(int pickupCommArea) { this.pickupCommArea = pickupCommArea; }
	public void setDropOffCommArea(int dropOffCommArea) { this.dropOffCommArea = dropOffCommArea; }
	public void setFare(double fare) { this.fare = fare; }
	public void setTips(double tips) { this.tips = tips; }
	public void setTolls(double toll) {this.tolls = toll; }
	public void setExtras(double extra) { this.extras = extra; }
	
	public void setTripTotalLength(int tripTotalLen) { this.TRIP_TOTAL_LENGTH = tripTotalLen; }
	public void setTripTotal(String tripTotal) { 
		this.tripTotal = tripTotal; 
		while(this.tripTotal.length() < TRIP_TOTAL_LENGTH)
			this.tripTotal += "#";							// '#' represents the default padding value
	}
	
	public void setPaymentTypeLength(int paymentTypeLen) { this.PAYMENT_TYPE_LENGTH = paymentTypeLen; }
	public void setPaymentType(String paymentType) { 
		this.paymentType = paymentType; 
		while(this.paymentType.length() < this.PAYMENT_TYPE_LENGTH)
			this.paymentType += "#";						// '#' represents the default padding value
	}
	
	public void setCompanyLength(int companyLen) { this.COMPANY_LENGTH = companyLen; }
	public void setCompany(String company) { 
		this.company = company; 
		while(this.company.length() < this.COMPANY_LENGTH)
			this.company += "#";							// '#' represents the default padding value
	}

	public void setPickupCenLat(double pickupLat) { this.pickupCenLat = pickupLat; }
	public void setPickupCenLon(double pickupLon) { this.pickupCenLon = pickupLon; }
	public void setPickupCenLocLength(int pickupLocLength) { this.PICKUP_CEN_LOC_LENGTH = pickupLocLength; }
	public void setPickupCenLoc(String pickupLoc) { 
		this.pickupCenLoc = pickupLoc; 
		while(this.pickupCenLoc.length() < this.PICKUP_CEN_LOC_LENGTH)
			this.pickupCenLoc += "#";						// '#' represents the default padding value
	}

	public void setDropOffCenLat(double dropOffLat) { this.dropOffCenLat = dropOffLat; }
	public void setDropOffCenLon(double dropOffLon) { this.dropOffCenLon = dropOffLon; }
	public void setDropOffCenLocLength(int dropOffLocLength) { this.DROPOFF_CEN_LOC_LENGTH = dropOffLocLength; }
	public void setDropOffCenLoc(String dropOffLoc) { 
		this.dropOffCenLoc = dropOffLoc; 
		while(this.dropOffCenLoc.length() < this.DROPOFF_CEN_LOC_LENGTH)
			this.dropOffCenLoc += "#";						// '#' represents the default padding value
	}	
	/*END Setters for data fields*/
	
	/*
	 * Writes the 4 int parameters to the end of the .bin file. This allows for a program that uses
	 * Prog1A.java to know how many bytes to skip to get to the TripTotals and other Taxi Fields.
	 */
	public void dumpInfo(RandomAccessFile stream, int numberOfRecords, int maxTripIDLen, int lengthBetweenIDandTotal, int maxTripTotalLen){
		try {
//			stream.seek(0);
			stream.writeInt(numberOfRecords);
			stream.writeInt(maxTripIDLen);
			stream.writeInt(lengthBetweenIDandTotal);
			stream.writeInt(maxTripTotalLen);
		} catch (IOException e) {
            System.out.println("I/O ERROR: Couldn't write to the file;\n\t"
                    + "perhaps the file system is full?");
            System.exit(-1);
		}
	}
	
	
	/* dumpObject(stream) -- write the content of the object's fields
     * to the file represented by the given RandomAccessFile object
     * reference.  Primitive types (e.g., int) are written directly.
     * Non-fixed-size values (e.g., strings) are converted to the
     * maximum allowed size before being written.  The result is a
     * file of uniformly-sized records.  Also note that text is
     * written with just one byte per character, meaning that we are
     * not supporting Unicode text.
     */
	public void dumpObject(RandomAccessFile stream){
		try {
			stream.writeBytes(this.tripID);
			stream.writeBytes(this.taxiID);
			stream.writeBytes(this.tripStart);
			stream.writeBytes(this.tripEnd);
			stream.writeInt(this.tripSeconds);
			stream.writeDouble(this.tripMiles);
			stream.writeDouble(this.pickupCenTract);
			stream.writeDouble(this.dropOffCenTract);
			stream.writeInt(this.pickupCommArea);
			stream.writeInt(this.dropOffCommArea);
			stream.writeDouble(this.fare);
			stream.writeDouble(this.tips);
			stream.writeDouble(this.tolls);
			stream.writeDouble(this.extras);
			stream.writeBytes(this.tripTotal);
			stream.writeBytes(this.paymentType);
			stream.writeBytes(this.company);
			stream.writeDouble(this.pickupCenLat);
			stream.writeDouble(this.pickupCenLon);
			stream.writeBytes(this.pickupCenLoc);
			stream.writeDouble(this.dropOffCenLat);
			stream.writeDouble(this.dropOffCenLon);
			stream.writeBytes(this.dropOffCenLoc);
		} catch (IOException e) {
            System.out.println("I/O ERROR: Couldn't write to the file;\n\t"
                    + "perhaps the file system is full?");
            System.exit(-1);
		}
	}
	
    /* fetchObject(stream) -- read the content of the object's fields
     * from the file represented by the given RandomAccessFile object
     * reference, starting at the current file position.  Primitive
     * types (e.g., int) are read directly.  To create Strings containing
     * the text, because the file records have text stored with one byte
     * per character, we can read a text field into an array of bytes and
     * use that array as a parameter to a String constructor.
     */
    public void fetchObject(RandomAccessFile stream)
    {
    	 //Note lengths may differ between different fields, but Strings within the same
    	 //field will have the same length
    	 byte[] tripIDLen = 		new byte[this.TRIP_ID_LENGTH];  		  // file -> byte[] -> String
         byte[] taxiIDLen = 		new byte[this.TAXI_ID_LENGTH];  		  // file -> byte[] -> String        
         byte[] tripStartLen = 		new byte[this.TRIP_START_LENGTH];  		  // file -> byte[] -> String
         byte[] tripEndLen = 		new byte[this.TRIP_END_LENGTH];  		  // file -> byte[] -> String
         byte[] tripTotalLen = 		new byte[this.TRIP_TOTAL_LENGTH];  		  // file -> byte[] -> String
         byte[] paymentTypeLen = 	new byte[this.PAYMENT_TYPE_LENGTH];  	  // file -> byte[] -> String
         byte[] companyLen = 		new byte[this.COMPANY_LENGTH];  		  // file -> byte[] -> String         
         byte[] pickupLocLen = 		new byte[this.PICKUP_CEN_LOC_LENGTH];	  // file -> byte[] -> String         
         byte[] dropOffLocLen = 	new byte[this.DROPOFF_CEN_LOC_LENGTH]; 	  // file -> byte[] -> String         
         
         try {
            stream.readFully(tripIDLen);
            this.tripID = new String(tripIDLen);
            stream.readFully(taxiIDLen);
            this.taxiID = new String(taxiIDLen);
            stream.readFully(tripStartLen);
            this.tripStart = new String(tripStartLen);
            stream.readFully(tripEndLen);
            this.tripEnd = new String(tripEndLen);
            this.tripSeconds = stream.readInt();
            this.tripMiles = stream.readDouble();
            this.pickupCenTract = stream.readDouble();
            this.dropOffCenTract = stream.readDouble();
            this.pickupCommArea = stream.readInt();
            this.dropOffCommArea = stream.readInt();
            this.fare = stream.readDouble();
            this.tips = stream.readDouble();
            this.tolls = stream.readDouble();
            this.extras = stream.readDouble();
            stream.readFully(tripTotalLen);
            this.tripTotal = new String(tripTotalLen);
            stream.readFully(paymentTypeLen);
            this.paymentType = new String(paymentTypeLen);
            stream.readFully(companyLen);
            this.company = new String(companyLen);
            this.pickupCenLat = stream.readDouble();
            this.pickupCenLon = stream.readDouble();
            stream.readFully(pickupLocLen);
            this.pickupCenLoc = new String(pickupLocLen);
            this.dropOffCenLat = stream.readDouble();
            this.dropOffCenLon = stream.readDouble();
            stream.readFully(dropOffLocLen);
            this.dropOffCenLoc = new String(dropOffLocLen);
            
        } catch (IOException e) {
            System.out.println("I/O ERROR: Couldn't read from the file;\n\t"
                             + "is the file accessible?");
            System.exit(-1);
         }
    }

}
