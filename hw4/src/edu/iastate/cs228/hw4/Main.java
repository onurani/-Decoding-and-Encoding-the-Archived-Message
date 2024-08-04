package edu.iastate.cs228.hw4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * 
 * @author ozairnurani
 * Implementation of main that reads a file from a arch file which contains an encoded scheme
 * and a binary encrypted message. The encoded string is then used to build a binary search tree
 * which is then used to build a character table for calling the printcode method in the 
 * Message Tree Class. We then call the decrpyt method in message tree class to obtain 
 * the human readable message.
 */
public class Main {
	public static void main(String[] args) throws IOException {
		
		String fileName = "";
		try {
			//ask the user to give a file name to be scanned
			System.out.println("Please enter filename to decode:");
			Scanner sc = new Scanner(System.in);
			fileName = sc.nextLine();
			sc.close();
		}
		catch(InputMismatchException e) {
			System.out.println("This is " + e.getMessage());
		}
			
			//the entire message from the file read into the string
			String bigword = "";
			//position of the last index of \n
			int lasttime = 0;
			//string with the encoded scheme given by the arch file
			String encodedscheme = "";
			//string with the binary scheme given by the arch file as the last line 
			String binaryscheme = "";
		
		try {
	             byte[] whole = Files.readAllBytes(Paths.get(fileName)); 
	             //need to trim to ensure the very last \n is removed 
	             bigword = new String(whole).trim();  
	             //the very last \n is after the encoding string and before the binary 
	             //encrypted message
	     		 lasttime = bigword.lastIndexOf('\n');
	        } 
		catch (IOException e) {
				System.out.println(e.getMessage());
	        }
		    //find the substring between the 0th character and the lasttime \n 
		    //character: that is our encoding scheme
		 	encodedscheme = bigword.substring(0, lasttime);
		 	//find the substring between the lasttime \n character and the endcharacter: 
		 	//that is our binary encrypted message
			binaryscheme = bigword.substring(lasttime, bigword.length()).trim();
		
			//created a character array to populate the string newword with the encoding string 
			//without the '^' so that it can be sent in the print codes method
			String newword = "";
			char[] carray = new char[encodedscheme.toCharArray().length];
			for(int j = 0; j < carray.length; j++) {
				carray[j] = encodedscheme.charAt(j);
			}
			for(int i = 0; i < carray.length; i++) {
				if(carray[i] != '^') {
					newword = newword + carray[i];
				}
			}
			
			//building a tree given the encoded string
			MsgTree root = new MsgTree(encodedscheme);
			//print the character and encoding table given the tree and the encoded string without 
			// the '^'
			MsgTree.printCodes(root, newword);
			//decodes the message into human readable form using the tree and binary encrypted message
			root.decryptmessage(root, binaryscheme);
	}
}