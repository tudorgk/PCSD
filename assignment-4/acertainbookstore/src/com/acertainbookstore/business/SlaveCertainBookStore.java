package com.acertainbookstore.business;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import com.acertainbookstore.interfaces.ReplicatedReadOnlyBookStore;
import com.acertainbookstore.interfaces.ReplicatedReadOnlyStockManager;
import com.acertainbookstore.utils.BookStoreException;
import com.acertainbookstore.utils.BookStoreMessageTag;
import com.acertainbookstore.utils.BookStoreResult;

/**
 * SlaveCertainBookStore is a wrapper over the CertainBookStore class and
 * supports the ReplicatedReadOnlyBookStore and ReplicatedReadOnlyStockManager
 * interfaces
 * 
 * This class must also handle replication requests sent by the master
 * 
 */
public class SlaveCertainBookStore implements ReplicatedReadOnlyBookStore,
		ReplicatedReadOnlyStockManager {
	private CertainBookStore bookStore = null;
	private long snapshotId = 0;

	public SlaveCertainBookStore() {
		bookStore = new CertainBookStore();
	}

	public synchronized BookStoreResult getBooks() throws BookStoreException {
		BookStoreResult result = new BookStoreResult(bookStore.getBooks(),
				snapshotId);
		return result;
	}

	public synchronized BookStoreResult getBooksInDemand()
			throws BookStoreException {
		throw new BookStoreException("Not implemented");
	}

	public synchronized BookStoreResult getBooks(Set<Integer> ISBNList)
			throws BookStoreException {
		BookStoreResult result = new BookStoreResult(
				bookStore.getBooks(ISBNList), snapshotId);
		return result;
	}

	public synchronized BookStoreResult getTopRatedBooks(int numBooks)
			throws BookStoreException {
		throw new BookStoreException("Not implemented");
	}

	public synchronized BookStoreResult getEditorPicks(int numBooks)
			throws BookStoreException {
		BookStoreResult result = new BookStoreResult(
				bookStore.getEditorPicks(numBooks), snapshotId);
		return result;
	}

	public BookStoreResult getBooksByISBN(Set<Integer> isbns)
			throws BookStoreException {
		BookStoreResult result = new BookStoreResult(
				bookStore.getBooksByISBN(isbns), snapshotId);
		return result;
	}
	public synchronized BookStoreResult addBooks(Set<StockBook> bookSet)
			throws BookStoreException {

		bookStore.addBooks(bookSet); // If this fails it will throw an exception
		snapshotId++;
		BookStoreResult result = new BookStoreResult(null, snapshotId);
		return result;
	}

	public synchronized BookStoreResult addCopies(Set<BookCopy> bookCopiesSet)
			throws BookStoreException {
		bookStore.addCopies(bookCopiesSet); // If this fails it will throw an
											// exception
		snapshotId++;
		BookStoreResult result = new BookStoreResult(null, snapshotId);
		return result;
	}

	public synchronized BookStoreResult updateEditorPicks(
			Set<BookEditorPick> editorPicks) throws BookStoreException {
		bookStore.updateEditorPicks(editorPicks); // If this fails it will throw
													// an exception
		snapshotId++;
		BookStoreResult result = new BookStoreResult(null, snapshotId);
		return result;
	}

	public synchronized BookStoreResult buyBooks(Set<BookCopy> booksToBuy)
			throws BookStoreException {
		
		bookStore.buyBooks(booksToBuy); // If this fails it will throw an
										// exception
		snapshotId++;
		BookStoreResult result = new BookStoreResult(null, snapshotId);
		return result;
	}

	public synchronized BookStoreResult rateBooks(Set<BookRating> bookRating)
			throws BookStoreException {
		throw new BookStoreException("Not implemented");
	}

	public BookStoreResult removeAllBooks() throws BookStoreException {
		
		System.out.println("REMOVING BOOKS");
		bookStore.removeAllBooks(); // If this fails it will throw an
									// exception
		snapshotId++;
		BookStoreResult result = new BookStoreResult(null, snapshotId);
		return result;
	}

	public BookStoreResult removeBooks(Set<Integer> isbnSet)
			throws BookStoreException {
		bookStore.removeBooks(isbnSet); // If this fails it will throw an
										// exception
		snapshotId++;
		BookStoreResult result = new BookStoreResult(null, snapshotId);
		return result;
	}

}
