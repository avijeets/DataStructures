package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		boolean hasBeenFound = false;
		while(!hasBeenFound) 
		{
			try 
			{
				hasBeenFound = true;
				Scanner drOfScanning = new Scanner(new File(docFile));
			}
			catch(FileNotFoundException e) 
			{
				System.err.println("File not found! " + e.getMessage());
				return new HashMap<String, Occurrence>();
			}
		}
		Scanner drOfScanning = new Scanner(new File(docFile));
		HashMap<String, Occurrence> hashMapOfKeyWord = new HashMap<String, Occurrence>();
		while(drOfScanning.hasNext()) 
		{
			String theNextWord = drOfScanning.next(); 
			theNextWord = getKeyWord(theNextWord); 
			if (theNextWord != null) 
			{ 
				if (hashMapOfKeyWord.containsKey(theNextWord))
					hashMapOfKeyWord.get(theNextWord).frequency++;
				else 
				{
					Occurrence refWord = new Occurrence(docFile, 1); 
					hashMapOfKeyWord.put(theNextWord, refWord);
				}
			}
		} return hashMapOfKeyWord;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		for (String keyToBeSet : kws.keySet()) 
		{ 
			Occurrence isKeyDone = kws.get(keyToBeSet);
			if (keywordsIndex.containsKey(keyToBeSet)) 
			{
				ArrayList<Occurrence> countPlusOne = keywordsIndex.get(keyToBeSet);
				
				countPlusOne.add(isKeyDone);
				insertLastOccurrence(countPlusOne);
				
				keywordsIndex.put(keyToBeSet, countPlusOne);
			}
			else 
			{ 
				ArrayList<Occurrence> countPlusTwo = new ArrayList<Occurrence>();
				
				countPlusTwo.add(isKeyDone);
				
				keywordsIndex.put(keyToBeSet, countPlusTwo);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	
	private boolean everyLetter(String para) {
		if (para.length() == 1) {
			if (Character.isLetter(para.charAt(0)))
				return true;
			return false;
		}
		else {
			if (Character.isLetter(para.charAt(para.length() - 1)))
				return everyLetter(para.substring(0, para.length() - 1));
			return false;
		}
	}
	
	private String forgetEnd(String para) {
		if (para.length() <= 1 || !punctuationBool(para.charAt(para.length() - 1)))
			return para;
		else
			return para = forgetEnd(para.substring(0, para.length() - 1));
	}
	
	private boolean punctuationBool (char punc) {
		if ( punc == ';' || 
				punc == '.' || 
				punc == '?' || 
				punc == ',' || 
				punc == '?' || 
				punc == '!' || 
				punc == ':')
			return true;
		else
			return false;
	}
	
	public String getKeyWord(String word) {
		word = word.trim(); 
		word = forgetEnd(word);
		
		for (String logicLoop : noiseWords.keySet()) {
			if (logicLoop.equalsIgnoreCase(word))
				return null;
		}
		
		word = word.toLowerCase();
		if (!everyLetter(word))
			return null;
		else
			return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		if (occs.size() == 1)
			return null;
		int first = 0; int last = occs.size() - 2; int median;
		int freqOfInsert = occs.get(occs.size() - 1).frequency; 
		ArrayList<Integer> medianI = new ArrayList<Integer>();
		while (last >= first) {
			median = (first + last) / 2;
			medianI.add(median);
			if (freqOfInsert > occs.get(median).frequency)
				last = median - 1;
			else if(freqOfInsert < occs.get(median).frequency)
				first = median + 1;
			else
				break;
		}
		
		/* test */
		if (medianI.get(medianI.size() - 1) == 0) 
		{
			if (freqOfInsert < occs.get(0).frequency) 
			{
				occs.add(1, occs.get(occs.size() - 1)); 
				occs.remove(occs.size() - 1);
				return medianI; 
			}
		}

		occs.add(medianI.get(medianI.size() - 1), occs.get(occs.size() - 1));
		occs.remove(occs.size() - 1);
		return medianI;
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	private boolean fiveLess(int num) {
		if (num <= 4) 
			return true;
		else
			return false;
	}
	
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<Occurrence> keywordListOne = new ArrayList<Occurrence>();
		ArrayList<String> fileArray = new ArrayList<String>();
		ArrayList<Occurrence> keywordListTwo = new ArrayList<Occurrence>();
		
		if (keywordsIndex.get(kw1.toLowerCase()) != null) { 
			keywordListOne = keywordsIndex.get(kw1.toLowerCase()); 
			if (keywordsIndex.get(kw2.toLowerCase()) == null) { 
				for (int a = 0; a < keywordListOne.size(); a++) { 
					if (fiveLess(fileArray.size()))
						fileArray.add(keywordListOne.get(a).document);
				}
				return fileArray;
			}
		}
		if (keywordsIndex.get(kw2.toLowerCase()) != null) {
			keywordListTwo = keywordsIndex.get(kw2.toLowerCase());
			if (keywordsIndex.get(kw1.toLowerCase()) == null) { 
				for (int a = 0; a < keywordListTwo.size(); a++) { 
					if (fiveLess(fileArray.size()))
						fileArray.add(keywordListTwo.get(a).document);
				}
				return fileArray;
			}
		}
		
		
		for (int i = 0; i < keywordListOne.size(); i++) {
			if (fiveLess(fileArray.size())) {
				String firstFile = keywordListOne.get(i).document;
				int percentFirstFile = keywordListOne.get(i).frequency;
				for (int j = 0; j < keywordListTwo.size(); j++) {
					String secFile = keywordListTwo.get(j).document;
					int percentSecFile = keywordListTwo.get(j).frequency;
					if (percentFirstFile > percentSecFile) {
						if (fiveLess(fileArray.size()) && !fileArray.contains(firstFile))
							fileArray.add(firstFile);
					}
					else if (percentSecFile == percentFirstFile) {
						if (fiveLess(fileArray.size())) {
							if (fileArray.size() == 4) {
								if (!fileArray.contains(secFile))
									fileArray.add(secFile);
								else if (!fileArray.contains(firstFile))
									fileArray.add(firstFile);
							}
							else {
								if (!fileArray.contains(firstFile))
									fileArray.add(firstFile);
								if (!fileArray.contains(secFile)) {
									fileArray.add(secFile);
								}
							}
						}
					}
					else if (percentFirstFile < percentSecFile) {
						if (fiveLess(fileArray.size()) && !fileArray.contains(secFile))
							fileArray.add(secFile);
					}
				}
				
			}
		}
		if (fileArray.size() < 1)
			return null;
		return fileArray;
	}
}
