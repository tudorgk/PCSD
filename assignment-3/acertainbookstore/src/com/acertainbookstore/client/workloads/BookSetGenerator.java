package com.acertainbookstore.client.workloads;

import java.util.*;

import com.acertainbookstore.business.ImmutableStockBook;
import com.acertainbookstore.business.StockBook;

/**
 * Helper class to generate stockbooks and isbns modelled similar to Random
 * class
 */
public class BookSetGenerator {

	HashMap<Integer, Boolean> isbns = new HashMap<Integer, Boolean>();
	HashMap<String, Boolean> titles = new HashMap<String, Boolean>();
	public BookSetGenerator() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns num randomly selected isbns from the input set
	 * 
	 * @param num
	 * @return
	 */
	public Set<Integer> sampleFromSetOfISBNs(Set<Integer> isbns, int num) {

		if(num>isbns.size()){
			//if the subset number is bigger than the set size return the entire set
			return isbns;
		}

		if (num < 0){
			//if the subset number is smaller than 0, return an empty set
			return new HashSet<Integer>();
		}

		ArrayList<Integer> originalISBNArray = new ArrayList<Integer> (isbns);
		ArrayList<Integer> subsetArray = new ArrayList<Integer>(num);

		/* Fill in subset array with first part of original array */
		for(int i=0;i<num;i++){
			subsetArray.add(i,originalISBNArray.get(i));
		}

		/*Go through the rest of the array*/
		for(int i=num;i<originalISBNArray.size();i++){
			int k = new Random().nextInt(i+1);
			if(k<num){
				subsetArray.set(k, originalISBNArray.get(i));
			}
		}
		return new HashSet<Integer>(subsetArray);
	}

	/**
	 * Return num stock books. For now return an ImmutableStockBook
	 * 
	 * @param num
	 * @return
	 */
	public Set<StockBook> nextSetOfStockBooks(int num) {
		Set<StockBook> result = new HashSet<StockBook>();
		int numCopies = 5;
		int SaleMisses = 0;
		int timesRated = 0;
		int totalRating = 0;
		String sampleCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
		for(int i=0;i<num;i++){
			int randomStringlength = new Random().nextInt(20) + 1;
			int ISBN = new Random().nextInt() + 1;
			// check if the isbn has been generated before
			while(isbns.containsKey(ISBN)){
				ISBN = new Random().nextInt() + 1;
			}
			// add the isbn to the hashmap to make sure we dont produce
			// the same isbn twice in the future
			isbns.put(ISBN, true);
			String title = generateString(new Random(),sampleCharacters,randomStringlength);
			// check if the title has been generated before
			while(titles.containsKey(title)){
				title = generateString(new Random(),sampleCharacters,randomStringlength);
				
			}
			// add to hashmap to make sure we dont produce the same title
			// twice in the future
			titles.put(title, true);
			String author = generateString(new Random(),sampleCharacters,randomStringlength);
			int price = new Random().nextInt(1000) + 1;
			boolean editorPick = new Random().nextBoolean();
			result.add(new ImmutableStockBook(ISBN, title, author, price, numCopies, SaleMisses, timesRated, totalRating, editorPick));
		}
		return result;
	}
	
	/*
	 * Generate a random string from a sample string
	 * Source: 
	 * http://stackoverflow.com/questions/2863852/how-to-generate-a-random-string-in-java
	 * */
	public static String generateString(Random rng, String characters, int length)
	{
	    char[] text = new char[length];
	    int limit = new Random(length).nextInt();
	    for (int i = 0; i < limit; i++)
	    {
	        text[i] = characters.charAt(rng.nextInt(characters.length()));
	    }
	    return new String(text);
	}

}
