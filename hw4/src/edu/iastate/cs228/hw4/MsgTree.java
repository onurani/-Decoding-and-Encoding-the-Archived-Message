package edu.iastate.cs228.hw4;

import java.util.Stack;

/**
 * 
 * @author ozairnurani
 *The MsgTree class which builds a tree using a stack and a binary search tree. This class also 
 *prints the character encoding table of the tree. This class also decrypts the binary message.
 *This class also prints the statistics of space savings.
 */

public class MsgTree {
	
		public char payloadChar;
		public MsgTree left;
		public MsgTree right;
		/*Can use a static char idx to the tree string for recursive
		solution, but it is not strictly necessary*/
		private static int staticCharIdx = 0;
		private static String wholeprintout;
		
		//Constructor building the tree from a string
		public MsgTree(String encodingString){
			//checking if the encoding string exists and has more than one character otherwise 
			//it is not worth building a tree from it 
			if(encodingString.length() <= 1 || encodingString == null) {
				return;
			}
			
			//using a stack to always know where the current node is so we can traverse in and
			//traverse out of the subtree. We do this by following the ^ by pushing it into the stack
			//or popping it out of the stack. 
			Stack<MsgTree> MsgTreestack = new Stack<>();
			//default direction set to left, if we change direction to right, set it false
			boolean myleftdirection = true;
			//total amount of characters
			int totalcharacters = 0;
			//sets the character in the string as payLoadChar to populate in the tree
			this.payloadChar = encodingString.charAt(totalcharacters++);
			//push the first character into the stack
			MsgTreestack.push(this);
			//cursor for the tree set to default root
			MsgTree herecursor = this;
			//go through each character in the string to populate the tree
			for(int j = totalcharacters; j < encodingString.length(); j++) {
				//treenode object created for each character in the string using the
				//constructor of the char Msg version
				MsgTree treenode = new MsgTree(encodingString.charAt(j));
				//if direction is to its left set treenode to the left of the here cursor
				if (myleftdirection) {
					herecursor.left = treenode;
					//if node is a '^' push it to the stack and set direction to left
					if (treenode.payloadChar == '^') {
						herecursor = MsgTreestack.push(treenode);
						myleftdirection = true;
					} else {
						//if its not empty then set the cursor to the item which was popped by the stack
						if (!MsgTreestack.empty())
							herecursor = MsgTreestack.pop();
						//change direction to right
						myleftdirection = false;
					}
				} 
				//if direction is to its right
				else { 
					herecursor.right = treenode;
					//if node is a '^' push it to the stack and set direction to left
					if (treenode.payloadChar == '^') {
						herecursor = MsgTreestack.push(treenode);
						myleftdirection = true;
					} 
					else {
						//if its not set the cursor to the item which was popped by the stack
						if (!MsgTreestack.empty()) 
							herecursor = MsgTreestack.pop();
						//change direction to right
						myleftdirection = false;
					}
				}
			}
		}
			
		//Constructor for a single node with null children
		public MsgTree(char payloadChar){
			this.payloadChar = payloadChar;
			this.left = null;
			this.right = null;
		}
		
		/**
		 * creates the character encoding chart
		 * method to print characters and their binary codes
		 * @param root
		 * @param code
		 */
		public static void printCodes(MsgTree root, String code){
			char letter;
			
			//using a loop for each character in the encrypted message call a 
			//recursive call method which takes in a root and letter to populate
			System.out.println("character code\n-------------------------");
			for(int i = 0; i < code.length(); i++) {
				letter = code.charAt(i);
				recursivecall(root, letter, "");
				String intermediate = "";
				//handling a special case for \n
				if(letter == '\n') {
					intermediate = "\\n";
				}
				else {
					intermediate = letter + " ";
				}
				System.out.println("    " + intermediate + "    " + wholeprintout);
			}
		}
		
		
		/**
		 * Method used to decrypt the binary message using the tree to make it into a 
		 * human readable form
		 * @param root
		 * @param binaryencryptedmessage
		 */
		public void decryptmessage(MsgTree root, String binaryencryptedmessage) {
			System.out.println("MESSAGE:");
			String  hwmessage = "";
			MsgTree currentrootnode = root;
			//looping through the binary encrypted message to find which way to go in the tree
			for (int i = 0; i < binaryencryptedmessage.length(); i++) {
				//read the payload character which can be either 0 or 1
				char individualletter = binaryencryptedmessage.charAt(i);
				//if 0 go left
				if(individualletter == '0') {
					currentrootnode = currentrootnode.left;
				}
				//if 1 go right
				else {
					currentrootnode = currentrootnode.right;
				}
				//if the character we are on in the tree is not '^' then add it to the 
				//human readable string hwmessage and reset the currentnode to root
				if (currentrootnode.payloadChar != '^') {
					hwmessage = hwmessage + currentrootnode.payloadChar;
					currentrootnode = root;
				}
			}
			System.out.println(hwmessage);
			//call to mystats method with binary encrpyted message and its 
			//decrypted human readable message to print statistics of space savings
			mystats(binaryencryptedmessage, hwmessage);
		}
		
		
		/**
		 * Recursive method which does a recursive call which accepts a tree, a individual letter
		 * from the encoded string and the edge path so as to traverse the tree, to populate 
		 * the edge path by going left and right depending on the case
		 * @param root
		 * @param individualletter
		 * @param edgepath
		 * @return
		 */
		private static boolean recursivecall(MsgTree root, char individualletter, String edgepath) {
			if (root != null) {
				//base case when the root payload matches the inputed individual letter from the
				//encoded string
				if (root.payloadChar == individualletter) {
					wholeprintout = edgepath;
					return true;
				}
				//recursive call going left
				recursivecall(root.left, individualletter, edgepath + "0");
				//recursive call going right
				recursivecall(root.right, individualletter, edgepath + "1");
				
			}
			return false;
		}
		
		
		/**
		 * Space savings calculation assumes that an uncompressed character is encoded with 
		 * 16 bits. It is defined as (1 – compressedBits/uncompressedBits)*100. 
		 * CompressedBits is the sum of all characters in the message multiplied by each 
		 * character's individual bits
		 * @param binaryencryptedmessage
		 * @param hwmessage
		 */
		private void mystats(String binaryencryptedmessage, String hwmessage) {
			//creating compressedBits and uncompressedBits, myresult and myspacesavings 
			//using formulas given in the homework
			int compressedBits = hwmessage.length();
			int uncompressedBits = binaryencryptedmessage.length();
			double myresult = uncompressedBits / (double) compressedBits;
			double myspacesavings = ((1 - hwmessage.length() / (double) binaryencryptedmessage.length()) * 100);
			//printing out the results
			System.out.println("STATISTICS:");
			System.out.println(String.format("Avg bits/char:         %.1f", myresult));
			System.out.println("Total Characters:      " + compressedBits);
			System.out.println(String.format("Space Saving:          %.1f%%", myspacesavings));
		}
}
