/**
 * 
 */
package com.acertainbookstore.client.workloads;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;

import com.acertainbookstore.business.Book;
import com.acertainbookstore.business.BookCopy;
import com.acertainbookstore.business.ImmutableStockBook;
import com.acertainbookstore.business.StockBook;
import com.acertainbookstore.utils.BookStoreException;

/**
 * 
 * Worker represents the workload runner which runs the workloads with
 * parameters using WorkloadConfiguration and then reports the results
 * 
 */
public class Worker implements Callable<WorkerRunResult> {
	private WorkloadConfiguration configuration = null;
	private int numSuccessfulFrequentBookStoreInteraction = 0;
	private int numTotalFrequentBookStoreInteraction = 0;

	public Worker(WorkloadConfiguration config) {
		configuration = config;
	}

	/**
	 * Run the appropriate interaction while trying to maintain the configured
	 * distributions
	 * 
	 * Updates the counts of total runs and successful runs for customer
	 * interaction
	 * 
	 * @param chooseInteraction
	 * @return
	 */
	private boolean runInteraction(float chooseInteraction) {
		try {
			if (chooseInteraction < configuration
					.getPercentRareStockManagerInteraction()) {
				runRareStockManagerInteraction();
			} else if (chooseInteraction < configuration
					.getPercentFrequentStockManagerInteraction()) {
				runFrequentStockManagerInteraction();
			} else {
				numTotalFrequentBookStoreInteraction++;
				runFrequentBookStoreInteraction();
				numSuccessfulFrequentBookStoreInteraction++;
			}
		} catch (BookStoreException ex) {
			return false;
		}
		return true;
	}

	/**
	 * Run the workloads trying to respect the distributions of the interactions
	 * and return result in the end
	 */
	public WorkerRunResult call() throws Exception {
		int count = 1;
		long startTimeInNanoSecs = 0;
		long endTimeInNanoSecs = 0;
		int successfulInteractions = 0;
		long timeForRunsInNanoSecs = 0;

		Random rand = new Random();
		float chooseInteraction;

		// Perform the warmup runs
		while (count++ <= configuration.getWarmUpRuns()) {
			chooseInteraction = rand.nextFloat() * 100f;
			runInteraction(chooseInteraction);
		}

		count = 1;
		numTotalFrequentBookStoreInteraction = 0;
		numSuccessfulFrequentBookStoreInteraction = 0;

		// Perform the actual runs
		startTimeInNanoSecs = System.nanoTime();
		while (count++ <= configuration.getNumActualRuns()) {
			chooseInteraction = rand.nextFloat() * 100f;
			if (runInteraction(chooseInteraction)) {
				successfulInteractions++;
			}
		}
		endTimeInNanoSecs = System.nanoTime();
		timeForRunsInNanoSecs += (endTimeInNanoSecs - startTimeInNanoSecs);
		return new WorkerRunResult(successfulInteractions,
				timeForRunsInNanoSecs, configuration.getNumActualRuns(),
				numSuccessfulFrequentBookStoreInteraction,
				numTotalFrequentBookStoreInteraction);
	}

	/**
	 * Runs the new stock acquisition interaction
	 * 
	 * @throws BookStoreException
	 */
	private void runRareStockManagerInteraction() throws BookStoreException {
		// get books in store
		List<StockBook> storeBooks = configuration.getStockManager().getBooks();
		// generate new books
		Set<StockBook> generatedBooks = configuration.getBookSetGenerator()
				.nextSetOfStockBooks(configuration.getNumBooksToAdd());
		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		// check if book already exists in store
		for(StockBook book : generatedBooks){
			if(!storeBooks.contains(book)){
				booksToAdd.add(book);
			}
		}
		// add books to the store
		configuration.getStockManager().addBooks(booksToAdd);
	}

	/**
	 * Runs the stock replenishment interaction
	 * 
	 * @throws BookStoreException
	 */
	private void runFrequentStockManagerInteraction() throws BookStoreException {
		// get books in store
		List<StockBook> storeBooks = configuration.getStockManager().getBooks();
		//sort list of books based on numCopies
		Comparator<StockBook> comparator = new Comparator<StockBook>(){
					@Override
					// I am not sure if the comparison is done this way
					// or the other way around.
					public int compare(StockBook o1, StockBook o2) {
						if(o1.getNumCopies() < o2.getNumCopies()){
							return 1;
						}else if(o1.getNumCopies() > o2.getNumCopies()){
							return -1;
						}
						return 0;
					}

		};
		Collections.sort(storeBooks,comparator);
		Set<BookCopy> copiesToAdd = new HashSet<BookCopy>();
		// we choose the k books with smallest quantities from the 
		// WorkloadConfiguration class
		for(int i=0; i<configuration.getNumBooksWithLeastCopies();i++){
			// make a BookCopy to add
			BookCopy temp = new BookCopy(storeBooks.get(i).getISBN(), 
					configuration.getNumAddCopies());
			copiesToAdd.add(temp);
		}
		
		// add copies of the books
		configuration.getStockManager().addCopies(copiesToAdd);
	}

	/**
	 * Runs the customer interaction
	 * 
	 * @throws BookStoreException
	 */
	private void runFrequentBookStoreInteraction() throws BookStoreException {
		// get editor picks
		List<Book> editorPicks = configuration.getBookStore()
				.getEditorPicks(configuration.getNumEditorPicksToGet());
		Set<Integer> isbns = new HashSet<Integer>();
		for(Book book : editorPicks){
			isbns.add(book.getISBN());
		}
		// sample editor picks
		Set<Integer> isbnsToBuy = configuration.getBookSetGenerator()
				.sampleFromSetOfISBNs(isbns, configuration.getNumBooksToBuy());
		List<StockBook> booksForSet = configuration.getStockManager().getBooksByISBN(isbnsToBuy);
		// create set for books to buy
		Set<BookCopy> booksToBuy = new HashSet<BookCopy>();
		for(StockBook book : booksForSet){
			booksToBuy.add(new BookCopy(book.getISBN(), 
					configuration.getNumBookCopiesToBuy()));
		}
		// buy books
		configuration.getBookStore().buyBooks(booksToBuy);
	}

}
