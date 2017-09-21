# Database-Project-2
Second Project of Database in Spring 2017 UofA

NOTES: 
1. The programs were ran and tested on linux machines.
2. Prog1A and Prog2 were written in Java using the Eclipse IDE

Purpose and Description:
There are two parts to the program. The first one called Prog1A, takes a CSV files and converts its contents into a binary file.
Although I took this class in Spring 2017, the project description is similar to the one posted for Fall 2017 found here
http://www2.cs.arizona.edu/classes/cs460/fall17/prog01.pdf

Prog2 takes the binary file created in Prog1A, converts its contents back to its original state and stores the information using Extendible Hashing. The program will then ask the user to input a Taxi Fare Price and will print out all Trip IDs, Trip Totals, and Byte Offset in which Trip Total matches the User input. NOTE: Input should be purely integers, no decimals. So if you want to find all matches for an $8.00 fare, then you would input 0800, since inputting 800 could also return a fare of $98.00.
Although I took this class in Spring 2017, the project description is similar to the one posted for Fall 2017 found here http://www2.cs.arizona.edu/classes/cs460/fall17/prog02.pdf

General Class Website for Fall 2017 is http://www2.cs.arizona.edu/classes/cs460/fall17/ (NOTE: I took this class in Spring 2017)

How to Run Prog1A:
1. Place all the files in the same directory
2. Use the command 'make' or 'make all'
3. Use the command 'java Prog1A name.csv' where name would be replaced by the name of the given .csv files
4. After step 3 is completed, a file called name.bin should be created (remember, name is replaced by whatever .csv file you used)

Example 'make'                                                                                                     
        'java Prog1A chicagotaxi-nov2016.csv'  (This should create a file called chicagotaxi-nov2016.bin)

How to Run Prog2
1. Use the command 'java Prog2 name.bin' where name would be replaced by the name of the given .bin file created
2. The program will prompt you enter an integer. The integer you are entering is the Taxi Fare and the program will print the Trip ID, Trip Total, and Byte Offset in which the Trip Total matches the input.
Note that when inputting a fare price, they are purely integers, no decimals. So if you want to find all matches for an $8.00 fare, then you would input 0800, since inputting 800 could also return a fare of $98.00. 

Example 'java Prog2 chicagotaxi-nov2016.bin'                                
        '0800'  (6274 Taxi Fares should match your $8.00 inquiry)                                            
        '800'   (7828 Taxi Fares should match your $*8.00 inquiry, where * means any integer matching 0-9)                           
        '5500'  (217 Taxi Fares should match your $55.00 inquiry)                                             
        'exit'  (Terminates program)                                 
