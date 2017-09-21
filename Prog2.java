import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Name: Ryunki Song
 * Class: CS460 Database
 * Professor: McCann
 * TA: Jacob, Yawen
 * Due Date: 2/15/2017 3:30 PM
 * Project 2
 * 
 * Description: Prog2.java takes in a binary file and parses the TRIP ID, TRIP TOTAL
 * and BYTE OFFSET (where the TRIP TOTAL is in the binary file) from each line
 * and stores the fields into a dynamic hash structure. The hash key is dependent
 * on the digit of the TRIP TOTAL (from right to left). Thus we have 10 pointers for
 * every node in the Tree (0-9). 
 * 
 * Once all the inserts have been made, you can search for TRIP IDs and BYTE OFFSETS
 * by entering a TRIP TOTAL and any matching TRIP TOTAL in the Tree will print out
 * the TRIP TOTAL itself, the TRIP ID and the BYTE OFFSET. 
 * 
 * How the program is formatted: First we have a basic Record class which stores
 * the total, key, and byte offset. They can be accessed using simple getter methods.
 * Then there is a Node class which hold buckets, and those buckets holds the records.
 * The Node class has an insert method to store records in the buckets, and a search
 * method which returns finds the matching Trip Total and prints out the trip total
 * and byte offset along with the trip total itself.
 * Lastly, we have the main method which conducts error checking from user input and uses
 * a infinite loop to prompt the user for input unless the user types 'exit'.
 * 
 * NOTE: The max size of each bucket is 6500, because the largest repeated trip total is
 * 8.00 which shows up 6273 times.  
 * 
 */



public class Prog2 {
	
	public static void main(String[] args){
		String fileName = args[0];						//Get file name from user
		Node node 	= new Node();						//Create the directory tree
		
		final int PARAM_COUNT = 1;						//How many parameters this program uses
		int maxTripTotalLength = 0;						//Used during search call for error checking

		
		//Variables to help us get the right fields from the binary file
		int recordLength, tripIDLength, tripTotalLength, lengthBetweenIDandTotal;

		if(args.length != PARAM_COUNT){
			System.err.println("This program uses one parameter, the name of the .bin file");
			System.exit(-1);
			return;
		}
				
		//Check if the file type is correct
		String temp = fileName.substring(fileName.length()-3, fileName.length());
		if(!temp.equals("bin")){
			System.err.println("Please use a .bin file.");
			System.exit(-1);
			return;
		}
		
		System.out.println("Processing Data from " + fileName + "...");
		
		/* Stage 1: Read in the file and print number of records and insert TripID, TripTotals and ByteOffset*/			
		String line = null;				//holds the value to be read in

		//1st get necessary data to read the .bin file
		try {
			RandomAccessFile dataStream = new RandomAccessFile(fileName,"rw");
			
			//Check for empty .bin file
			if(dataStream.length() < 40){
				System.err.println("Empty .bin file");
				System.exit(-1);
				dataStream.close();
				return;
			}

			
			dataStream.seek((dataStream.length() - 16));
			recordLength 			= dataStream.readInt();
			tripIDLength 			= dataStream.readInt();
			lengthBetweenIDandTotal = dataStream.readInt();
			tripTotalLength 		= dataStream.readInt();

			
//			System.out.println(recordLength + " " + tripIDLength + " " + tripTotalLength + " " + lengthBetweenIDandTotal); //DEBUG print
			if(recordLength<1 || tripIDLength<1 || tripTotalLength<1){
				System.err.println("File has no information to store.");
				System.exit(-1);
				dataStream.close();
				return;
			}
			
			dataStream.close();
		} catch (FileNotFoundException e) {
			System.err.println("File could not be found.");
			System.exit(-1);
			return;
		} catch (IOException e) {
			System.err.println("Input error.");
			System.exit(-1);
			return;
		}
		
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
        	//Check for an empty file
        	line = br.readLine();
			if (line == null) {
        	    System.out.println("File is empty");
        	    br.close();
        	    System.exit(-1);
        	    return;
        	}
			
			/*
			 * This is where we parse through the binary file and retrieve and store
			 * all tripIDs, tripTotals, byteOffset, into the Directory Tree
			 */
			RandomAccessFile stream = new RandomAccessFile(fileName, "rw");
			int bytesUsed = 0;
			
			//These lists are used to store the fields so that we can insert them into the tree later.
			//We need to do this because we don't know what the max trip total is yet (and thus its length of digits).
			//The elements in the lists will later be removed.
			ArrayList<String>  tripIDList    = new ArrayList<String>();
			ArrayList<String>  tripTotalList = new ArrayList<String>();
			ArrayList<Integer> byteOffsetList = new ArrayList<Integer>(); 
			
			//Get the fields and insert
			while(bytesUsed < stream.length()-16){
				
				byte[] tripIDLen = new byte[tripIDLength];					//First field is trip id so read it in
				stream.readFully(tripIDLen);								//Get the entire line
				String tripID = new String(tripIDLen);						//Convert the line into a string
				
				byte[] lengthBetween = new byte[lengthBetweenIDandTotal];	//Prepare to skip
				stream.readFully(lengthBetween);							//Skip to the trip total field
				
				byte[] tripTotal = new byte[tripTotalLength];				//Now we are at the trip total field
				stream.readFully(tripTotal);								//Read the line in
				String tripTotalString = new String(tripTotal);				//convert line into a string
				String cleanTripTotal = "0";								//The prefix to be inserted, need to have default 0 placeholder at the beginning of every digit

				//We have our string, but we need to sanitize it to make sure we handle any potential empty fields denoted by '#'
				char[] charArray = tripTotalString.toCharArray();			
				for (int i = 0; i < charArray.length; i++) {
					if (charArray[i] != '#' && charArray[i] != '.')
						cleanTripTotal += charArray[i];
				}
				if(cleanTripTotal.equals(""))								//Check to see if this field was empty, if so, then use 0.00
					cleanTripTotal = "0100";								//Default value for empty total, represents 1.00 (extra 0 is needed for the insert method to work in class Node)
				
				if(cleanTripTotal.length() > maxTripTotalLength)			//Update max length 
					maxTripTotalLength = cleanTripTotal.length();
				
				int byteOffset = tripIDLength + lengthBetweenIDandTotal + bytesUsed; //Setup byteOffset parameter
								
				//Add the info to the lists
				tripIDList.add(tripID);
				tripTotalList.add(cleanTripTotal);
				byteOffsetList.add(byteOffset);
				
				//Now that we have grabbed the two fields from the line, we can skip to the next line.
				int bytesToSkip = tripIDLength + lengthBetweenIDandTotal + tripTotalLength;		//Get the amount of bytes we need to skip to next line
				if((recordLength-bytesToSkip) < 0){
					System.err.println("Please input correct numbers.");
					System.exit(-1);
				}
				byte[] skipToNextLine = new byte[recordLength-bytesToSkip];	//Prepare to skip	
				stream.readFully(skipToNextLine);	
				
				bytesUsed += recordLength;									//While loop condition update
			}
			stream.close();
			
			/*
			 * Now we know the max length of a string so now we need to insert the field.
			 * NOTE: The three array lists share the same size.
			 */
			for(int i=0; i<tripIDList.size(); i++){
				String tripID	 = tripIDList.get(i);
				String tripTotal = tripTotalList.get(i);
				int byteOffset 	 = byteOffsetList.get(i);
				
				String cleanTripTotal = "";									//The prefix to be inserted, need to have default 0 placeholder at the beginning of every digit
				int diff = maxTripTotalLength - tripTotal.length();			//Get how many leading 0's we need to make every tripTotal entry the same length
				for(int j=0; j<diff; j++)
					cleanTripTotal += '0';									//Add the zeros
				
				char[] arrayTemp = tripTotal.toCharArray();					//Set up the string to be added to the cleanTripTotal
				for(int j=0; j<arrayTemp.length; j++)
					cleanTripTotal += arrayTemp[j];							//Now add the prefix to the string to be inserted
				node.insert(cleanTripTotal, tripID, byteOffset);			//Now insert the information in the node
//				System.out.println(cleanTripTotal + " " + tripID + " " + byteOffset);	//DEBUT print
			}
			
			//Everything is in the Directory Tree so delete every element in the list
			tripIDList = new ArrayList<String>();
			tripTotalList = new ArrayList<String>();
			byteOffsetList = new ArrayList<Integer>();
			
		} catch (FileNotFoundException e) {
			System.err.println("File could not be found.");
			System.exit(-1);
			return;
		} catch (IOException e) {
			System.err.println("Input error.");
			System.exit(-1);
			return;
		}
		
		//Now everything has been inserted. Now we need to prompt the user for some suffixes
		System.out.println("Processed binary file. Please enter some prefixes to search. Type 'exit' if you would like to terminate the search.");
		
		Scanner scan = new Scanner(System.in);
		while(scan.hasNext()){
			String input = scan.next();									//Scan in user input
			
			System.out.println("Finding matches for " + input + "...");

			if(input.equals(""))										//Empty input case
				System.out.println("No matches could be found.");
			if(input.length() > maxTripTotalLength)						//Entering an number larger than the data set
				System.out.println("No matches could be found.");
		
			String prefix = "0";										//The prefix to be searched, need to have default 0 placeholder at the beginning of every digit
			char[] array = input.toCharArray();
			for(int i=0; i<array.length; i++){
				if(array[i] != '0' && array[i] != '1' && array[i] != '2' && array[i] != '3' && array[i] != '4' && 
					array[i] != '5' && array[i] != '6' && array[i] != '7' && array[i] != '8' && array[i] != '9'){
					
					//NOTE: values of -1 was treated as $1.00
					System.out.println("Please enter non-negative integer values. If you are searching for"
									+ " -1, then enter 1.");
					break;
				}
				else
					prefix += array[i];
			}
			//System.out.println(prefix);									//DEBUG PRINT
			
			//Terminate if user wants to exit
			if(input.equals("exit")){
				System.out.println("Terminating program...\nDone.");
				scan.close();	
				return;
			}
			if(!prefix.equals("0"))
				node.search(input);
		}
		scan.close();
	}
}


/**
 * Records are the elements of the buckets/node data structure. 
 * Each record holds the total, key, and byte offset (points to where
 * in the binary file the total can be found). 
 * Has simple getters methods for each field. 
 * 
 * @author cantstoptheunk
 *
 */
class Record{
	//Declare fields we want to store
	private int total, key, byteOffset;
	private String totalStr, tripIDStr;
	
	//Setup the total and totalString variable
	public Record(String totalStr, String tripIDStr, int byteOffset, int level){	
		this.total = Integer.parseInt(totalStr.substring(1, totalStr.length()));							//Set total
		this.totalStr = totalStr;						//Set String total
		this.byteOffset = byteOffset;					//Set byte Offset
		this.tripIDStr = tripIDStr;						//Set trip ID
		
		setKey(total, level);							//Set key
	}
	
	private void setKey(int total, int level){
		char[] array = totalStr.toCharArray();			//Get the array from the String total
		char charKey = 99;								//Initialize charKey to a default value
		
		if(level == 0)
			level = 1;									//This is a specific case when making a new hash dictionary for the first time
		
		if(array.length - level < 0){					//Check if the bucket size is too small
			System.err.println("Bucket size is too small. Need to make it bigger.");
			System.exit(-1);
			return;
		}
		charKey = array[(array.length-level)];			//Grab the correct digit
		int key = charKey-48;							//CharKey needs to be converted from a ASCII value to the value it is
		
		this.key = key;									//Set the key value
	}
	
	public int getKey(){
		return key;
	}
	
	public int getTotal(){
		return total;
	}
	
	public int getByteOffset(){
		return byteOffset;
	}
	
	public String getStringTotal(){
		return totalStr;
	}
	public String getStringID(){
		return tripIDStr;
	}
}


/**
 * Node holds all the arrays which hold the buckets.
 * Each Node is distinguished by their level which determines which digits 
 * place becomes the key from the trip total in the taxi data.
 * 
 * When any given array reaches the max size then we create a new node and use 
 * the next digits place as the new key at the given level.
 * 
 * @author cantstoptheunk
 *
 */

class Node{
	public static int MAX_SIZE = 6500;		//Set max size
	private int count, level, nodeKey;		//Count represents total records in the bucket, level determines which digits place is being used, nodeKey represents what key its bucket holds
	private boolean hasBucket;				//Need this boolean to distinguish nodes that has buckets or are just pointer nodes
	
	
	//Declare all nodes pointing to the next level of nodes
	public Node next0, next1, next2, next3, next4, next5, next6, next7, next8, next9;
	
	//Declare the array that holds the buckets in this node
	private Record[] bucket;
	
	//This constructor is used whenever you want to create node pointers with no buckets
	public Node(){
		this.level = 0;
		
		int nextLevel = level + 1;
		next0 = new Node(nextLevel, 0);
		next1 = new Node(nextLevel, 1);
		next2 = new Node(nextLevel, 2);
		next3 = new Node(nextLevel, 3);
		next4 = new Node(nextLevel, 4);
		next5 = new Node(nextLevel, 5);
		next6 = new Node(nextLevel, 6);
		next7 = new Node(nextLevel, 7);
		next8 = new Node(nextLevel, 8);
		next9 = new Node(nextLevel, 9);
		hasBucket = false;
	}
	
	//This constructor is used whenever you want to create node pointers with buckets
	public Node(int level, int nodeKey){
		this.level = level;
		this.nodeKey = nodeKey;
		
		bucket = new Record[MAX_SIZE];	count = 0; 	//Create array and set its respective count
		
		next0 = next1 = next2 = next3 = next4 = next5 = next6 = next7 = next8 = next9 = null;
		hasBucket = true;
	}

	public boolean hasBucket(){
		return hasBucket;
	}
	
	//Return how many elements are in the bucket respective to the key value
	public int getBucketCount(){
		return count;
	}

	
	
	/*
	 * This method is called whenever an insert on a Trip Total needs to be made.
	 * It automatically extracts the key and inserts the record in the appropriate bucket.
	 * 
	 * HOW IT WORKS: If the current node can use its bucket, then we can simply insert
	 * the key and trip total into the bucket.
	 * However, if the current node cannot use its bucket, then we have to traverse further,
	 * until we reach a node that can use a bucket to insert the key and trip total.
	 * 
	 * Parameters: The fields we want to store
	 */
	public void insert(String totalStr, String tripIDStr, int byteOffset){
		int nextLevel = level + 1;
		Record record = new Record(totalStr, tripIDStr, byteOffset, nextLevel);	// Create the record to be inserted
		
		int key = record.getKey(); 								// Get the key
//		System.out.println(level + " " + key);					//DEBUG PRINT
		
		//Traverse if current node cannot insert information into its bucket
		if(!hasBucket){
			if(key == 0)
				this.next0.insert(totalStr, tripIDStr, byteOffset);
			else if(key == 1)
				this.next1.insert(totalStr, tripIDStr, byteOffset);
			else if(key == 2)
				this.next2.insert(totalStr, tripIDStr, byteOffset);
			else if(key == 3)
				this.next3.insert(totalStr, tripIDStr, byteOffset);
			else if(key == 4)
				this.next4.insert(totalStr, tripIDStr, byteOffset);
			else if(key == 5)
				this.next5.insert(totalStr, tripIDStr, byteOffset);
			else if(key == 6)
				this.next6.insert(totalStr, tripIDStr, byteOffset);
			else if(key == 7)
				this.next7.insert(totalStr, tripIDStr, byteOffset);
			else if(key == 8)
				this.next8.insert(totalStr, tripIDStr, byteOffset);
			else if(key == 9)
				this.next9.insert(totalStr, tripIDStr, byteOffset);
			else{
				System.err.println("Non-valid key input");
				System.exit(-1);
				return;
			}
		}
		//Else insert the record in a bucket based on the key
		else {											
			if(count >= MAX_SIZE){
				reformat();							//Reformat the structure
				hasBucket = false;					//This node no longer should be used hold records
				
				Record nextRecord = new Record(totalStr, tripIDStr, byteOffset, nextLevel);	//Create a new record to be inserted
				int nextKey = nextRecord.getKey(); 	//Get the key from the next digit
								
				if(nextKey == 0)
					this.next0.insert(totalStr, tripIDStr, byteOffset);
				else if(nextKey == 1)
					this.next1.insert(totalStr, tripIDStr, byteOffset);
				else if(nextKey == 2)
					this.next2.insert(totalStr, tripIDStr, byteOffset);
				else if(nextKey == 3)
					this.next3.insert(totalStr, tripIDStr, byteOffset);
				else if(nextKey == 4)
					this.next4.insert(totalStr, tripIDStr, byteOffset);
				else if(nextKey == 5)
					this.next5.insert(totalStr, tripIDStr, byteOffset);
				else if(nextKey == 6)
					this.next6.insert(totalStr, tripIDStr, byteOffset);
				else if(nextKey == 7)
					this.next7.insert(totalStr, tripIDStr, byteOffset);
				else if(nextKey == 8)
					this.next8.insert(totalStr, tripIDStr, byteOffset);
				else if(nextKey == 9)
					this.next9.insert(totalStr, tripIDStr, byteOffset);
				else{
					System.err.println("Non-valid key input");
					System.exit(-1);
					return;
				}
			}
			else{
				bucket[count] = record;				// Insert the record
				count++; 							// Increment total count
			}
		}	
	}
	
	/*
	 * This method is called by the insert method whenever any given bucket reaches is filled up.
	 * Then we have to reformat the given node and remove its bucket and add new pointers 
	 * to the next level. 
	 * 
	 * How it works: Take every element in the bucket of the current node, and insert it 
	 * into the next level, depending on the new digit. Once we have finished, make 
	 * sure this node can no longer insert buckets.
	 */
	private void reformat(){		
		hasBucket = false;		//This node no longer has buckets because it has been filled
		
		int nextLevel = level + 1;
		//Create the next level
		next0 = new Node(nextLevel, 0);
		next1 = new Node(nextLevel, 1);
		next2 = new Node(nextLevel, 2);
		next3 = new Node(nextLevel, 3);
		next4 = new Node(nextLevel, 4);
		next5 = new Node(nextLevel, 5);
		next6 = new Node(nextLevel, 6);
		next7 = new Node(nextLevel, 7);
		next8 = new Node(nextLevel, 8);
		next9 = new Node(nextLevel, 9);
		
		//Now we have to re-insert the records in the this node's bucket into the next level. 
		for(int i=0; i<MAX_SIZE; i++){
//			int total = bucket[i].getTotal(); 							//Grab the total to be inserted
			String totalStr 	= bucket[i].getStringTotal();			//Grab the total to be inserted
			String tripIDStr 	= bucket[i].getStringID();				//Grab ID to be inserted
			int byteOffset 		= bucket[i].getByteOffset();			//Grab byte offset to be inserted
			
			Record record = new Record(totalStr, tripIDStr, byteOffset, nextLevel);
			int nextKey = record.getKey(); 								//Get the key from the next digit
			
			//Insert into appropriate level
			if (nextKey == 0)
				this.next0.insert(totalStr, tripIDStr, byteOffset);
			else if (nextKey == 1)
				this.next1.insert(totalStr, tripIDStr, byteOffset);
			else if (nextKey == 2)
				this.next2.insert(totalStr, tripIDStr, byteOffset);
			else if (nextKey == 3)
				this.next3.insert(totalStr, tripIDStr, byteOffset);
			else if (nextKey == 4)
				this.next4.insert(totalStr, tripIDStr, byteOffset);
			else if (nextKey == 5)
				this.next5.insert(totalStr, tripIDStr, byteOffset);
			else if (nextKey == 6)
				this.next6.insert(totalStr, tripIDStr, byteOffset);
			else if (nextKey == 7)
				this.next7.insert(totalStr, tripIDStr, byteOffset);
			else if (nextKey == 8)
				this.next8.insert(totalStr, tripIDStr, byteOffset);
			else if (nextKey == 9) 
				this.next9.insert(totalStr, tripIDStr, byteOffset);
			else {
				System.err.println("Error in reformatting the structure.");
				System.exit(-1);
				return;
			}
			
		}
	}
	
	/*
	 * This method returns all the numbers matching the prefix passed in.
	 * 
	 * The digits in the prefix from the last digit to the first tells us
	 * where to traverse in our query. For each digit, we traverse deeper
	 * until we run out of digits. Once we run out of digits
	 * we have to consider the following cases:
	 * 
	 * CASE 1: The node we are at once we finish traversing has a true value for 
	 * the hasBucket boolean variable. In this case, we can simply just print out 
	 * the bucket in the node and be done. 
	 * 
	 * CASE 2: The node we are at has a false value for the hasBucket variable.
	 * In this case, we have to recursively print out every child node's bucket 
	 * that has a true value for its hasBucket variable. If a child node's
	 * hasBucket is false, we have to recursively continue deeper until we reach a
	 * node that has a true value for hasBucket. We do this for every child node of
	 * the current one. 
	 * 
	 * Parameter: The trip total prefix to search for matches
	 * 
	 */
	public void search(String prefix){
		char[] array = prefix.toCharArray();
		
		//Conduct error checking for non-numeric entries
		for(int i=0; i<array.length; i++){
			if(array[i] != '0' && array[i] != '1' && array[i] != '2' && array[i] != '3' 
					&& array[i] != '4' && array[i] != '5' && array[i] != '6' 
					&& array[i] != '7' && array[i] != '8' && array[i] != '9'){
				System.err.println("No matches could be found.");
				return;
			}
		}
		
		//Traverse through the Directory tree using the prefix
		int digit = array.length-1;						//What digit's place we begin
		Node node = this;								//Get a bird's eye view of its own world
		while(digit > -1 && !node.hasBucket){
			int key = array[digit]-48;					//Get the key and then compare
//			System.out.println(key);					//DEBUG PRINT
			//Traverse to the correct Directory
			if(key == 0)
				node = node.next0;
			else if(key == 1)
				node = node.next1;
			else if(key == 2)
				node = node.next2;
			else if(key == 3)
				node = node.next3;
			else if(key == 4)
				node = node.next4;
			else if(key == 5)
				node = node.next5;
			else if(key == 6)
				node = node.next6;
			else if(key == 7)
				node = node.next7;
			else if(key == 8)
				node = node.next8;
			else if(key == 9)
				node = node.next9;
			else{
				System.err.println("Error in searching for your matches.");
				System.exit(-1);
				return;
			}
			digit--;
		}
		
		
		int matchingSearches = 0;							//Number of Matching searches
		//Now we need to deal with our two cases
		//CASE 1: Print out the records in the bucket
		if(node.hasBucket){
			for(int i=0; i<node.getBucketCount(); i++){
				String totStr = node.bucket[i].getStringTotal();
				totStr = totStr.substring(1, totStr.length());
				if(totStr.contains(prefix)){
					System.out.println("Trip ID: " + node.bucket[i].getStringID() + ", Trip Total: " 
						+ node.bucket[i].getTotal() + ", Byte Offset: " + node.bucket[i].getByteOffset());
					matchingSearches++;
				}
			}
			System.out.println(matchingSearches + " records matched your query.");
		}
		//CASE 2: Need to traverse through every child of the node and print its records in each bucket
		else{
			matchingSearches = recursiveTraverse(node);
			System.out.println(matchingSearches + " records matched your query.");
		}

		
	}
	
	/*
	 * This method is executed whenever we have traversed the tree using all of our prefix digits 
	 * but have more child nodes that fit the prefix. Thus we have to recurse through 
	 * every node and print out their contents. 
	 * 
	 * There is no need to check if the prefix is conatined because by traversing to this
	 * point, we already know that every element in every child nodes contains the prefix.
	 * 
	 * Parameter: Takes in a Node which is used to traverse recursively.
	 * Return: Returns the number of matching searches for a given trip total prefix
	 */
	private int recursiveTraverse(Node node){
		int matchingSearches = 0;
		
		//We can print the child since it is the deepest child leaf for a specific digit path
		if(node.hasBucket){
			int count = 0;
			for(int i=0; i<node.getBucketCount(); i++){
					System.out.println("Trip ID: " + node.bucket[i].getStringID() + ", Trip Total: " 
						+ node.bucket[i].getTotal() + ", Byte Offset: " + node.bucket[i].getByteOffset());
					count++;
			}
			return count;
		}
		//Otherwise keep traversing until you reach a leaf child
		else if(node != null){
//			System.out.println("DEBUG RECURSE");
			matchingSearches += recursiveTraverse(node.next0);
			matchingSearches += recursiveTraverse(node.next1);
			matchingSearches += recursiveTraverse(node.next2);
			matchingSearches += recursiveTraverse(node.next3);
			matchingSearches += recursiveTraverse(node.next4);
			matchingSearches += recursiveTraverse(node.next5);
			matchingSearches += recursiveTraverse(node.next6);
			matchingSearches += recursiveTraverse(node.next7);
			matchingSearches += recursiveTraverse(node.next8);
			matchingSearches += recursiveTraverse(node.next9);
		}
		return matchingSearches;
	}
}
