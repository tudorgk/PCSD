/**
 * 
 */
package com.acertainbookstore.business;

import java.util.*;
import java.util.Map.Entry;

import com.acertainbookstore.interfaces.BookStore;
import com.acertainbookstore.interfaces.StockManager;
import com.acertainbookstore.utils.BookStoreConstants;
import com.acertainbookstore.utils.BookStoreException;
import com.acertainbookstore.utils.BookStoreUtility;

/**
 * CertainBookStore implements the bookstore and its functionality which is
 * defined in the BookStore
 */
public class CertainBookStore implements BookStore, StockManager {
	//singleton for the books store
	private static CertainBookStore singleInstance;
	private Map<Integer, BookStoreBook> bookMap = null;

	public CertainBookStore() {
		// Constructors are not synchronized
		bookMap = new HashMap<Integer, BookStoreBook>();
	}

	public synchronized CertainBookStore getInstance() {
		if (singleInstance != null) {
			return singleInstance;
		} else {
			singleInstance = new CertainBookStore();
			bookMap = new HashMap<Integer, BookStoreBook>();
		}
		return singleInstance;
	}

	public synchronized void addBooks(Set<StockBook> bookSet)
			throws BookStoreException {

		if (bookSet == null) {
			throw new BookStoreException(BookStoreConstants.NULL_INPUT);
		}
		// Check if all are there
		for (StockBook book : bookSet) {
			int ISBN = book.getISBN();
			String bookTitle = book.getTitle();
			String bookAuthor = book.getAuthor();
			int noCopies = book.getNumCopies();
			float bookPrice = book.getPrice();
			if (BookStoreUtility.isInvalidISBN(ISBN)
					|| BookStoreUtility.isEmpty(bookTitle)
					|| BookStoreUtility.isEmpty(bookAuthor)
					|| BookStoreUtility.isInvalidNoCopies(noCopies)
					|| bookPrice < 0.0) {
				throw new BookStoreException(BookStoreConstants.BOOK
						+ book.toString() + BookStoreConstants.INVALID);
			} else if (bookMap.containsKey(ISBN)) {
				throw new BookStoreException(BookStoreConstants.ISBN + ISBN
						+ BookStoreConstants.DUPLICATED);
			}
		}

		for (StockBook book : bookSet) {
			int ISBN = book.getISBN();
			bookMap.put(ISBN, new BookStoreBook(book));
		}
		return;
	}

	public synchronized void addCopies(Set<BookCopy> bookCopiesSet)
			throws BookStoreException {
		int ISBN, numCopies;

		if (bookCopiesSet == null) {
			throw new BookStoreException(BookStoreConstants.NULL_INPUT);
		}

		for (BookCopy bookCopy : bookCopiesSet) {
			ISBN = bookCopy.getISBN();
			numCopies = bookCopy.getNumCopies();
			if (BookStoreUtility.isInvalidISBN(ISBN))
				throw new BookStoreException(BookStoreConstants.ISBN + ISBN
						+ BookStoreConstants.INVALID);
			if (!bookMap.containsKey(ISBN))
				throw new BookStoreException(BookStoreConstants.ISBN + ISBN
						+ BookStoreConstants.NOT_AVAILABLE);
			if (BookStoreUtility.isInvalidNoCopies(numCopies))
				throw new BookStoreException(BookStoreConstants.NUM_COPIES
						+ numCopies + BookStoreConstants.INVALID);

		}

		BookStoreBook book;
		// Update the number of copies
		for (BookCopy bookCopy : bookCopiesSet) {
			ISBN = bookCopy.getISBN();
			numCopies = bookCopy.getNumCopies();
			book = bookMap.get(ISBN);
			book.addCopies(numCopies);
		}
	}

	public synchronized List<StockBook> getBooks() {
		List<StockBook> listBooks = new ArrayList<StockBook>();
		Collection<BookStoreBook> bookMapValues = bookMap.values();
		for (BookStoreBook book : bookMapValues) {
			listBooks.add(book.immutableStockBook());
		}
		return listBooks;
	}

	public synchronized void updateEditorPicks(Set<BookEditorPick> editorPicks)
			throws BookStoreException {
		// Check that all ISBNs that we add/remove are there first.
		if (editorPicks == null) {
			throw new BookStoreException(BookStoreConstants.NULL_INPUT);
		}

		int ISBNVal;

		for (BookEditorPick editorPickArg : editorPicks) {
			ISBNVal = editorPickArg.getISBN();
			if (BookStoreUtility.isInvalidISBN(ISBNVal))
				throw new BookStoreException(BookStoreConstants.ISBN + ISBNVal
						+ BookStoreConstants.INVALID);
			if (!bookMap.containsKey(ISBNVal))
				throw new BookStoreException(BookStoreConstants.ISBN + ISBNVal
						+ BookStoreConstants.NOT_AVAILABLE);
		}

		for (BookEditorPick editorPickArg : editorPicks) {
			bookMap.get(editorPickArg.getISBN()).setEditorPick(
					editorPickArg.isEditorPick());
		}
		return;
	}

	public synchronized void buyBooks(Set<BookCopy> bookCopiesToBuy)
			throws BookStoreException {
		if (bookCopiesToBuy == null) {
			throw new BookStoreException(BookStoreConstants.NULL_INPUT);
		}

		// Check that all ISBNs that we buy are there first.
		int ISBN;
		BookStoreBook book;
		Boolean saleMiss = false;
		for (BookCopy bookCopyToBuy : bookCopiesToBuy) {
			ISBN = bookCopyToBuy.getISBN();
			if (bookCopyToBuy.getNumCopies() < 0)
				throw new BookStoreException(BookStoreConstants.NUM_COPIES
						+ bookCopyToBuy.getNumCopies()
						+ BookStoreConstants.INVALID);
			if (BookStoreUtility.isInvalidISBN(ISBN))
				throw new BookStoreException(BookStoreConstants.ISBN + ISBN
						+ BookStoreConstants.INVALID);
			if (!bookMap.containsKey(ISBN))
				throw new BookStoreException(BookStoreConstants.ISBN + ISBN
						+ BookStoreConstants.NOT_AVAILABLE);
			book = bookMap.get(ISBN);
			if (!book.areCopiesInStore(bookCopyToBuy.getNumCopies())) {
				book.addSaleMiss(); // If we cannot sell the copies of the book
									// its a miss
				saleMiss = true;
			}
		}

		// We throw exception now since we want to see how many books in the
		// order incurred misses which is used by books in demand
		if (saleMiss)
			throw new BookStoreException(BookStoreConstants.BOOK
					+ BookStoreConstants.NOT_AVAILABLE);

		// Then make purchase
		for (BookCopy bookCopyToBuy : bookCopiesToBuy) {
			book = bookMap.get(bookCopyToBuy.getISBN());
			book.buyCopies(bookCopyToBuy.getNumCopies());
		}
		return;
	}

	public synchronized List<StockBook> getBooksByISBN(Set<Integer> isbnSet)
			throws BookStoreException {
		if (isbnSet == null) {
			throw new BookStoreException(BookStoreConstants.NULL_INPUT);
		}
		for (Integer ISBN : isbnSet) {
			if (BookStoreUtility.isInvalidISBN(ISBN))
				throw new BookStoreException(BookStoreConstants.ISBN + ISBN
						+ BookStoreConstants.INVALID);
			if (!bookMap.containsKey(ISBN))
				throw new BookStoreException(BookStoreConstants.ISBN + ISBN
						+ BookStoreConstants.NOT_AVAILABLE);
		}

		List<StockBook> listBooks = new ArrayList<StockBook>();

		for (Integer ISBN : isbnSet) {
			listBooks.add(bookMap.get(ISBN).immutableStockBook());
		}

		return listBooks;
	}

	public synchronized List<Book> getBooks(Set<Integer> isbnSet)
			throws BookStoreException {
		if (isbnSet == null) {
			throw new BookStoreException(BookStoreConstants.NULL_INPUT);
		}
		// Check that all ISBNs that we rate are there first.
		for (Integer ISBN : isbnSet) {
			if (BookStoreUtility.isInvalidISBN(ISBN))
				throw new BookStoreException(BookStoreConstants.ISBN + ISBN
						+ BookStoreConstants.INVALID);
			if (!bookMap.containsKey(ISBN))
				throw new BookStoreException(BookStoreConstants.ISBN + ISBN
						+ BookStoreConstants.NOT_AVAILABLE);
		}

		List<Book> listBooks = new ArrayList<Book>();

		// Get the books
		for (Integer ISBN : isbnSet) {
			listBooks.add(bookMap.get(ISBN).immutableBook());
		}
		return listBooks;
	}

	public synchronized List<Book> getEditorPicks(int numBooks)
			throws BookStoreException {
		if (numBooks < 0) {
			throw new BookStoreException("numBooks = " + numBooks
					+ ", but it must be positive");
		}

		List<BookStoreBook> listAllEditorPicks = new ArrayList<BookStoreBook>();
		List<Book> listEditorPicks = new ArrayList<Book>();
		Iterator<Entry<Integer, BookStoreBook>> it = bookMap.entrySet()
				.iterator();
		BookStoreBook book;

		// Get all books that are editor picks
		while (it.hasNext()) {
			Entry<Integer, BookStoreBook> pair = (Entry<Integer, BookStoreBook>) it
					.next();
			book = (BookStoreBook) pair.getValue();
			if (book.isEditorPick()) {
				listAllEditorPicks.add(book);
			}
		}

		// Find numBooks random indices of books that will be picked
		Random rand = new Random();
		Set<Integer> tobePicked = new HashSet<Integer>();
		int rangePicks = listAllEditorPicks.size();
		if (rangePicks <= numBooks) {
			// We need to add all the books
			for (int i = 0; i < listAllEditorPicks.size(); i++) {
				tobePicked.add(i);
			}
		} else {
			// We need to pick randomly the books that need to be returned
			int randNum;
			while (tobePicked.size() < numBooks) {
				randNum = rand.nextInt(rangePicks);
				tobePicked.add(randNum);
			}
		}

		// Get the numBooks random books
		for (Integer index : tobePicked) {
			book = listAllEditorPicks.get(index);
			listEditorPicks.add(book.immutableBook());
		}
		return listEditorPicks;

	}

	@Override
	public synchronized List<Book> getTopRatedBooks(int numBooks)
			throws BookStoreException {

		//create a comparator class that sorts the books by rating
		class StockBookCompareRating implements Comparator<StockBook> {
			@Override
			public int compare(StockBook book0, StockBook book1) {
				return (int) (book1.getAverageRating() - book0.getAverageRating());
			}
		}

		if (numBooks < 0) {
			throw new BookStoreException("numBooks = " + numBooks
					+ ", but it must be positive");
		}
		numBooks = Math.min(numBooks, bookMap.values().size());
		// Filter top rated
		List<StockBook> books = getBooks(); // can return a BookStoreException
		Collections.sort(books, new StockBookCompareRating());

		List<Book> topRated = new ArrayList<Book>();
		int isbn;

		for (int i=0; i<numBooks; i++) {
			isbn = books.get(i).getISBN();
			topRated.add(bookMap.get(isbn).immutableBook());
		}
		return topRated;
	}

	@Override
	public synchronized List<StockBook> getBooksInDemand()
			throws BookStoreException {
		List<StockBook> books = getBooks();
		List<StockBook> inDemand = new ArrayList<StockBook>();
		int isbn;

		for (StockBook book : books) {
			if (book.getSaleMisses() > 0) {
				isbn = book.getISBN();
				inDemand.add(bookMap.get(isbn).immutableStockBook()); //add an immutable stock book
			}
		}
		return inDemand;
	}

	@Override
	public synchronized void rateBooks(Set<BookRating> bookRating)
			throws BookStoreException {
		// All or nothing
		for (BookRating rating : bookRating) {
			//if the rating is valid proceed..
			if (!(0 <= rating.getRating() && rating.getRating() <= 5)
					|| !bookMap.containsKey(rating.getISBN())) {
				throw new BookStoreException(BookStoreConstants.ISBN + rating.getISBN()
						+ BookStoreConstants.NOT_AVAILABLE);
			}
		}

		// rating is valid
		for (BookRating rating : bookRating) {
			bookMap.get(rating.getISBN()).addRating(rating.getRating());
		}
	}

	public synchronized void removeAllBooks() throws BookStoreException {
		bookMap.clear();
	}

	public synchronized void removeBooks(Set<Integer> isbnSet)
			throws BookStoreException {

		if (isbnSet == null) {
			throw new BookStoreException(BookStoreConstants.NULL_INPUT);
		}
		for (Integer ISBN : isbnSet) {
			if (BookStoreUtility.isInvalidISBN(ISBN))
				throw new BookStoreException(BookStoreConstants.ISBN + ISBN
						+ BookStoreConstants.INVALID);
			if (!bookMap.containsKey(ISBN))
				throw new BookStoreException(BookStoreConstants.ISBN + ISBN
						+ BookStoreConstants.NOT_AVAILABLE);
		}

		for (int isbn : isbnSet) {
			bookMap.remove(isbn);
		}
	}
}
