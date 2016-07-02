package apps;

import java.io.IOException;
import java.util.Scanner;
import structures.Node;

/**
 * This class sorts a given list of strings which represent numbers in
 * the given radix system. For instance, radix=10 means decimal numbers;
 * radix=16 means hexadecimal numbers. 
 * 
 * @author ru-nb-cs112
 */
public class Radixsort {

	/**
	 * Master list that holds all items, starting with input, and updated after every pass
	 * of the radixsort algorithm. Holds sorted result after the final pass. This is a
	 * circular linked list in which every item is stored in its textual string form (even
	 * though the items represent numbers). This masterListRear field points to the last 
	 * node in the CLL.
	 */
	Node<String> masterListRear;
	
	/**
	 * Array of linked lists that holds the digit-wise distribution of the items during
	 * each pass of the radixsort algorithm. 
	 */
	Node<String>[] buckets;
	
	/** 
	 * The sort radix, defaults to 10.
	 */
	int radix = 10;
	
	/**
	 * Initializes this object with the given radix (10 or 16)
	 * 
	 * @param radix
	 */
	public Radixsort() {
		masterListRear = null;
		buckets = null;
	}
	
	/**
	 * Sorts the items in the input file, and returns a CLL containing the sorted result
	 * in ascending order. The first line in the input file is the radix. Every subsequent
	 * line is a number, to be read in as a string.
	 * 
	 * The items in the input are first read and stored in the master list, which is a CLL that is referenced
	 * by the masterListRear field. Next, the max number of digits in the items is determined. Then, 
	 * scatter and gather are called, for each pass through the items. Pass 0 is for the least
	 * significant digit, pass 1 for the second-to-least significant digit, etc. After each pass,
	 * the master list is updated with items in the order determined at the end of that pass.
	 * 
	 * NO NEW NODES are created in the sort process - the nodes of the master list are recycled
	 * through all the intermediate stages of the sorting process.
	 * 
	 * @param sc Scanner that points to the input file of radix + items to be sorted
	 * @return Sorted (in ascending order) circular list of items
	 * @throws IOException If there is an exception in reading the input file
	 */
	public Node<String> sort(Scanner sc) throws IOException {
				if (!sc.hasNext()) {
					return null;
				}
				radix = sc.nextInt();
				buckets = (Node<String>[])new Node[radix];
				createMasterListFromInput(sc);
				int maxDigits = getMaxDigits();
				for (int i = 0; i < maxDigits; i++) {
					scatter(i);
					gather();
				}
				return masterListRear;
			}
	/**
	 * Reads entries to be sorted from input file and stores them as 
	 * strings in the master CLL (pointed by the instance field masterListRear, 
	 * in the order in which they are read. In other words, the first entry in the linked 
	 * list is the first entry in the input, the second entry in the linked list is the 
	 * second entry in the input, and so on. 
	 * 
	 * @param sc Scanner pointing to the input file
	 * @throws IOException If there is any error in reading the input
	 */
	public void createMasterListFromInput(Scanner sc) throws IOException {
		sc.nextLine();
		Node <String> front = new <String> Node (sc.nextLine(),null);
		front.next = front;
		masterListRear = front;
		while (sc.hasNext()== true){
			Node <String> tempVal = new <String> Node (sc.nextLine(), front);
			masterListRear.next= tempVal;
			masterListRear = masterListRear.next;
		}
	}
	/**
	 * Determines the maximum number of digits over all the entries in the master list
	 * 
	 * @return Maximum number of digits over all the entries
	 */
	public int getMaxDigits() {
		int maxDigits = masterListRear.data.length();
		Node<String> ptr = masterListRear.next;
		while (ptr != masterListRear) {
			int length = ptr.data.length();
			if (length > maxDigits) {
				maxDigits = length;
			}
			ptr = ptr.next;
		}
		return maxDigits;
	}
	/**
	 * Scatters entries of master list (referenced by instance field masterListReat) 
	 * to buckets for a given pass.
	 * 
	 * Passes are digit by digit, starting with the rightmost digit -
	 * the rightmost digit is the "0-th", i.e. pass=0 for rightmost digit, pass=1 for 
	 * second to rightmost, and so on. 
	 * 
	 * Each digit is extracted as a character, 
	 * then converted into the appropriate numeric value in the given radix
	 * using the java.lang.Character.digit(char ch, int radix) method
	 * 
	 * @param pass Pass is 0 for rightmost digit, 1 for second to rightmost, etc
	 */
	public void scatter(int pass) {
		Node<String> ptrOneofThree = masterListRear;
		Sorter.printCLL(masterListRear);
		do {
			if ((ptrOneofThree.data.length() - pass - 1) < 0) {
				if (buckets[0] == null) {
					Node <String> firstPtr;
					buckets[0] = ptrOneofThree;
					firstPtr = ptrOneofThree;
					ptrOneofThree = ptrOneofThree.next;
					firstPtr.next=firstPtr;
				}
				else {
					Node ptrSecond = buckets[0];
					while (ptrSecond.next != buckets[0]) {
						ptrSecond = ptrSecond.next;
					}
					Node <String> firstPtr;
					firstPtr = ptrOneofThree;
					ptrOneofThree=ptrOneofThree.next;
					firstPtr.next = buckets[0];
					ptrSecond.next = firstPtr;
				} 
			}
			else{
			int digChar = Character.digit(ptrOneofThree.data.charAt(ptrOneofThree.data.length() - pass-1), radix);
			if (buckets[digChar] == null) {
				Node <String> firstPtr;
				buckets[digChar] = ptrOneofThree;
				firstPtr = ptrOneofThree;
				ptrOneofThree = ptrOneofThree.next;
				firstPtr.next=firstPtr;
			} 
			else {
				Node ptrThird = buckets[digChar];
				while (ptrThird.next != buckets[digChar]) {
					ptrThird = ptrThird.next;
				}
				Node <String> firstPtr;
				firstPtr=ptrOneofThree;
				ptrOneofThree=ptrOneofThree.next;
				ptrThird.next = firstPtr;
				firstPtr.next = buckets[digChar];
				}
			}
		} while (ptrOneofThree != masterListRear);
	}

	/**
	 * Gathers all the CLLs in all the buckets into the master list, referenced
	 * by the instance field masterListRear
	 * 
	 * @param buckets Buckets of CLLs
	 */
	public void gather() {
		masterListRear = null;
		Node <String> firstPtr = masterListRear;
		for(int i = 0; i < buckets.length; i++){
			if(buckets[i] == null){
				continue;
			}
			else {
				if(masterListRear == null){
					masterListRear=buckets[i];
					firstPtr=masterListRear;
				}
				else{
					firstPtr.next=buckets[i];
					firstPtr=firstPtr.next;
				}
				while(firstPtr.next != buckets[i]){
					firstPtr = firstPtr.next;
				}
			}
		}
		for(int i = 0; i<buckets.length; i++) {
			buckets[i]=null;
		}
		firstPtr.next=masterListRear;
		masterListRear=firstPtr;
		Sorter.printCLL(masterListRear);
	}
}	
