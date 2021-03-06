package com.acertainbookstore.client.workloads;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.*;

import com.acertainbookstore.business.ImmutableStockBook;
import com.acertainbookstore.business.StockBook;

/**
 * Helper class to generate stockbooks and isbns modelled similar to Random
 * class
 */
public class BookSetGenerator {

	HashMap<Integer, Boolean> isbns = new HashMap<Integer, Boolean>();
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

		for(int i=0;i<num;i++){

			int ISBN = new Random().nextInt(10000000) + 10000000;
			// check if the isbn has been generated before
			while(isbns.containsKey(ISBN)){
				ISBN = new Random().nextInt(10000000) + 10000000;
			}
			// add the isbn to the hashmap to make sure we dont produce
			// the same isbn twice in the future
			isbns.put(ISBN, true);

			//add the title, price and author with the same value of the ISBN
			String title = Integer.toString(ISBN);
			String author = Integer.toString(ISBN);
			int price = ISBN;

			boolean editorPick = new Random().nextBoolean();

			result.add(new ImmutableStockBook(ISBN, title, author, price, numCopies, SaleMisses, timesRated, totalRating, editorPick));
		}
		return result;
	}

}
