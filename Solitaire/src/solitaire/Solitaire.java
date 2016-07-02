package solitaire;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;
import java.util.NoSuchElementException;

/**
 * This class implements a simplified version of Bruce Schneier's Solitaire Encryption algorithm.
 * 
 * @author RU NB CS112
 */
public class Solitaire {
	
	/**
	 * Circular linked list that is the deck of cards for encryption
	 */
	CardNode deckRear;
	
	/**
	 * Makes a shuffled deck of cards for encryption. The deck is stored in a circular
	 * linked list, whose last node is pointed to by the field deckRear
	 */
	public void makeDeck() {
		// start with an array of 1..28 for easy shuffling
		int[] cardValues = new int[28];
		// assign values from 1 to 28
		for (int i=0; i < cardValues.length; i++) {
			cardValues[i] = i+1;
		}
		
		// shuffle the cards
		Random randgen = new Random();
 	        for (int i = 0; i < cardValues.length; i++) {
	            int other = randgen.nextInt(28);
	            int temp = cardValues[i];
	            cardValues[i] = cardValues[other];
	            cardValues[other] = temp;
	        }
	     
	    // create a circular linked list from this deck and make deckRear point to its last node
	    CardNode cn = new CardNode();
	    cn.cardValue = cardValues[0];
	    cn.next = cn;
	    deckRear = cn;
	    for (int i=1; i < cardValues.length; i++) {
	    	cn = new CardNode();
	    	cn.cardValue = cardValues[i];
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
	    }
	}
	
	/**
	 * Makes a circular linked list deck out of values read from scanner.
	 */
	public void makeDeck(Scanner scanner) 
	throws IOException {
		CardNode cn = null;
		if (scanner.hasNextInt()) {
			cn = new CardNode();
		    cn.cardValue = scanner.nextInt();
		    cn.next = cn;
		    deckRear = cn;
		}
		while (scanner.hasNextInt()) {
			cn = new CardNode();
	    	cn.cardValue = scanner.nextInt();
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
		}
	}
	
	/**
	 * Implements Step 1 - Joker A - on the deck.
	 */
	void jokerA() {
			boolean jokerOne = false;
		    boolean jokerTwo = false;
		    if(deckRear.next == null || deckRear == null)
		    	throw new NoSuchElementException();   
		    for (CardNode ptr = deckRear.next;ptr != deckRear; ptr = ptr.next){
		    	if(deckRear.cardValue == 27)
		    		jokerOne = true;
		    	if (ptr.cardValue == 27)
		    		jokerOne = true;
		    	if(deckRear.cardValue == 28)
		    		jokerTwo = true;
		    	if(ptr.cardValue == 28)
		    		jokerTwo = true;
		    }
		    
		    if(jokerOne == false || jokerTwo == false)
		    	throw new NoSuchElementException();
		    
		    CardNode prevNodeVal = deckRear;
		    
		for (CardNode currLoopVal = deckRear.next; currLoopVal != deckRear; currLoopVal = currLoopVal.next) {
			if(currLoopVal.next == deckRear && currLoopVal.next.cardValue == 27) {
				CardNode portableVal = deckRear;
				currLoopVal.next = deckRear.next;
				deckRear = currLoopVal.next;
				CardNode portableValTwo = deckRear.next;
				deckRear.next = portableVal;
				portableVal.next = portableValTwo;
				return;
			}
			else if (currLoopVal.cardValue == 27) {
				CardNode portableVal = currLoopVal;
				CardNode portableValTwo = currLoopVal.next.next;
				prevNodeVal.next = currLoopVal.next;
				prevNodeVal.next.next = portableVal;
				portableVal.next = portableValTwo;
				return;
			}
			
			else if (currLoopVal.next == deckRear && currLoopVal.cardValue == 27){
				CardNode portableVal = currLoopVal;
				CardNode header = deckRear.next;
				CardNode ender = deckRear;
				
				prevNodeVal.next = currLoopVal.next;
				ender.next = portableVal;
				
				deckRear = portableVal;
				portableVal.next = header;
				return;
				
			}
			
			prevNodeVal = prevNodeVal.next;
		}
	}
	
	/**
	 * Implements Step 2 - Joker B - on the deck.
	 */
	void jokerB() {

	    if(deckRear == null)
	    	throw new NoSuchElementException();
		CardNode previousNodeVal = deckRear;
		
		for(CardNode currIVal = deckRear.next; currIVal != deckRear; currIVal = currIVal.next){
			if(currIVal.next.next == deckRear && currIVal.cardValue == 28){
				CardNode ender = deckRear;
				CardNode portableValOne = currIVal;
				CardNode header = deckRear.next;
				
				previousNodeVal.next = currIVal.next;
				ender.next = portableValOne;
				deckRear = portableValOne;
				portableValOne.next = header;
				return;
				
			}
			else if(currIVal.next.cardValue == 28 && currIVal.next == deckRear) {
				CardNode portableValOne = deckRear;
				currIVal.next = deckRear.next;
				deckRear = currIVal.next;
				
				CardNode portableValTwo = deckRear.next;
				deckRear.next = portableValOne;
				portableValOne.next = portableValTwo;
				
				CardNode portableValThree = portableValOne;
				deckRear.next = portableValTwo;
				
				CardNode portableValFour = portableValTwo.next;
				portableValTwo.next = portableValThree;
				portableValThree.next = portableValFour;
				return;
			}
			
			else if (currIVal.cardValue == 28) {
				CardNode portableValOne = currIVal;
				CardNode portableValTwo = currIVal.next.next.next;
				
				previousNodeVal.next = currIVal.next;
				previousNodeVal.next.next.next = portableValOne;
				portableValOne.next = portableValTwo;
				return;
			}
			else if (currIVal.next == deckRear && currIVal.cardValue == 28 ) {
				CardNode portableValOne = currIVal;
				previousNodeVal.next = currIVal.next;
				
				CardNode portableValTwo = deckRear.next;
				deckRear.next = portableValOne;
				deckRear = portableValOne;
				deckRear.next = portableValTwo;
				
				portableValOne = deckRear;
				previousNodeVal.next.next = deckRear.next;
				deckRear = previousNodeVal.next.next;
				portableValTwo = deckRear.next;
				deckRear.next = portableValOne;
				portableValOne.next = portableValTwo;
				return;
			}
			else
				previousNodeVal = previousNodeVal.next;
		}	
	}
	
	/**
	 * Implements Step 3 - Triple Cut - on the deck.
	 */
	void tripleCut() {
		CardNode prevNode = deckRear.next;
		CardNode currentNode = deckRear.next.next;
		
		if(deckRear == null)
	    	throw new NoSuchElementException();
		
		if (deckRear.cardValue == 28 || deckRear.cardValue == 27) {
			prevNode = deckRear;
			currentNode = deckRear.next;
			for(currentNode = deckRear.next; currentNode != deckRear; currentNode = currentNode.next){
				if(currentNode.cardValue == 28 || currentNode.cardValue == 27){
					CardNode header = deckRear.next;
					CardNode portableValOne = currentNode;
					CardNode portableValTwo = prevNode;
					
					deckRear.next = header;
					deckRear = portableValTwo;
					deckRear.next = portableValOne;
					return;
				}
				else if(deckRear.next.cardValue == 28 || deckRear.next.cardValue == 27)
					return;
				else
					prevNode = prevNode.next;
			}
		}
	 
		else if(deckRear.next.cardValue == 28 || deckRear.next.cardValue == 27){
			for(currentNode = deckRear.next.next; currentNode != deckRear; currentNode = currentNode.next){
				if (deckRear.cardValue == 27 || deckRear.cardValue == 28)
					return;
				else if (currentNode.cardValue == 28 || currentNode.cardValue == 27){
					CardNode portableOne = currentNode;
					CardNode portableTwo = currentNode.next;
					deckRear = portableOne;
					deckRear.next = portableTwo;
					return;
				}
				else 
					prevNode = prevNode.next;
			}
		}
		
		else {
			prevNode = deckRear;
			currentNode = deckRear.next;
			while(currentNode != deckRear){
				if(currentNode.cardValue == 28 || currentNode.cardValue == 27){
					CardNode firstJoker = currentNode;
					CardNode currentNodeTwo = currentNode.next;
					while(currentNodeTwo != deckRear.next){
						if(currentNodeTwo.cardValue == 28 || currentNodeTwo.cardValue == 27){
							CardNode secondJoker = currentNodeTwo;
							CardNode head = deckRear.next;
							CardNode subsequentSecond = currentNodeTwo.next;
							
							deckRear.next = firstJoker;
							secondJoker.next = head;
							
							deckRear = prevNode;
							deckRear.next = subsequentSecond;
							
							return;
						}
						else
							currentNodeTwo = currentNodeTwo.next;
					}
				}
				else{
					prevNode = prevNode.next;
					currentNode = currentNode.next;
				}
			}
		}
	}
	
	/**
	 * Implements Step 4 - Count Cut - on the deck.
	 */
	void countCut() {	
		int ender;
		int twentyeight = deckRear.cardValue; 
		if(deckRear == null || deckRear.next == null)
	    	throw new NoSuchElementException();
		if(twentyeight == 28)
			ender = 27; 
		else 
			ender = deckRear.cardValue;

		CardNode header = deckRear.next;
		CardNode lastVal = deckRear;
		CardNode pos = deckRear.next;
		
		int count = 0;
		
		CardNode rearMedian = new CardNode();
		CardNode endCount = new CardNode();
		CardNode medianVal = new CardNode();

		while(count <= ender) {
			if(count == (ender-1)) {
				if(pos.next == deckRear)
					return;
				
				endCount = pos;
				medianVal = pos.next;

				for(CardNode lastVal2 = pos.next; lastVal2 != deckRear; lastVal2 = lastVal2.next) {
					if(lastVal2.next == deckRear) {
						rearMedian = lastVal2;
						rearMedian.next = null;
						lastVal2.next = header;
						endCount.next = deckRear;
						deckRear.next = medianVal;
						return;
					}
				}
			}
			pos = pos.next;
			lastVal = lastVal.next;
			count++;
		}
		
	}
	
	/**
	 * Utility method that prints a circular linked list, given its rear pointer
	 * 
	 * @param rear Rear pointer
	 */
	 private static void printList(CardNode rear) {
		if (rear == null) { 
			return;
		}
		System.out.print(rear.next.cardValue);
		CardNode ptr = rear.next;
		do {
			ptr = ptr.next;
			System.out.print("," + ptr.cardValue);
		} while (ptr != rear);
		System.out.println("\n");
	}
	 
	/**
	 * Implements Step 5 - key = INCLUDING repeating the whole process if the key turns
	 * out to be a joker.
	 * 
	 * @return Key between 1 and 26
	 */
	int getKey() {
		int headerVal = deckRear.next.cardValue;
		int count = 1;
		int encrypt = -1;
		CardNode posVal = deckRear.next;
		
		if(headerVal == 28)
			headerVal = 27;
		
		while(posVal != deckRear) {
			if(count == headerVal) {
				if(posVal.next.cardValue == 28 || posVal.next.cardValue == 27) {
					
					jokerA();
					jokerB();
					tripleCut();
					countCut();
					
					posVal = deckRear;
					count = 0;
					
					headerVal = deckRear.next.cardValue;

					if(headerVal == 28)
						headerVal = 27;
				}
				else {
					encrypt = posVal.next.cardValue;
					return encrypt;
				}
			}
			posVal = posVal.next;
			count++;
		}
		return encrypt;
	}
	
	/**
	 * Encrypts a message, ignores all characters except upper case letters
	 * 
	 * @param message Message to be encrypted
	 * @return Encrypted message, a sequence of upper case letters only
	 */
	public String encrypt(String message) {	
		String finalSay;
		String concatDat;
		finalSay = "";
		concatDat = message.replaceAll("[^a-zA-Z]","");

		for(int i = 0;i < concatDat.length();i++) {
			char twentysix = Character.toUpperCase(concatDat.charAt(i));
			System.out.println(twentysix);
			int hexa = twentysix-'A'+1;
			
			jokerA();
			jokerB();
			tripleCut();
			countCut();

			int key = getKey();
			int sum = hexa + key;

			if(sum > 26)
				sum -= 26;
			
			twentysix = (char)(sum-1+'A');
			
			System.out.println(twentysix);
			finalSay += twentysix;
		}
		
	    return finalSay;
	}
	
	/**
	 * Decrypts a message, which consists of upper case letters only
	 * 
	 * @param message Message to be decrypted
	 * @return Decrypted message, a sequence of upper case letters only
	 */
	public String decrypt(String message) {	
		String finalSay;
		String concatDat;
		finalSay = "";
		concatDat = message.replaceAll("[^a-zA-Z]","");
		for(int i = 0;i < concatDat.length();i++) {
			char twentysix = Character.toUpperCase(concatDat.charAt(i));
			int hexa = twentysix - 'A' + 1;
			
			jokerA();
			jokerB();
			tripleCut();
			countCut();

			int encrypt = getKey();
			int full = hexa - encrypt;

			if(full <= 0)
				full += 26;
			
			twentysix = (char)(full-1+'A');
			finalSay += twentysix;
		}
	    return finalSay;
	}
}