package com.acertainbookstore.client.tests;

import static org.junit.Assert.*;

import java.util.*;

import com.acertainbookstore.business.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.acertainbookstore.client.BookStoreHTTPProxy;
import com.acertainbookstore.client.StockManagerHTTPProxy;
import com.acertainbookstore.interfaces.BookStore;
import com.acertainbookstore.interfaces.StockManager;
import com.acertainbookstore.utils.BookStoreConstants;
import com.acertainbookstore.utils.BookStoreException;

/**
 * Test class to test the BookStore interface
 * 
 */
public class BookStoreTest {

	private static final int TEST_ISBN = 3044560;
	private static final int NUM_COPIES = 5;
	private static boolean localTest = true;
	private static StockManager storeManager;
	private static BookStore client;

	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			String localTestProperty = System
					.getProperty(BookStoreConstants.PROPERTY_KEY_LOCAL_TEST);
			localTest = (localTestProperty != null) ? Boolean
					.parseBoolean(localTestProperty) : localTest;
			if (localTest) {
				CertainBookStore store = new CertainBookStore();
				storeManager = store;
				client = store;
			} else {
				storeManager = new StockManagerHTTPProxy(
						"http://localhost:8081/stock");
				client = new BookStoreHTTPProxy("http://localhost:8081");
			}
			storeManager.removeAllBooks();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Helper method to add some books
	 */
	public void addBooks(int isbn, int copies) throws BookStoreException {
		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		StockBook book = new ImmutableStockBook(isbn, "Test of Thrones",
				"George RR Testin'", (float) 10, copies, 0, 0, 0, false);
		booksToAdd.add(book);
		storeManager.addBooks(booksToAdd);
	}

	/**
	 * Helper method to get the default book used by initializeBooks
	 */
	public StockBook getDefaultBook() {
		return new ImmutableStockBook(TEST_ISBN, "Harry Potter and JUnit",
				"JK Unit", (float) 10, NUM_COPIES, 0, 0, 0, false);
	}

	/**
	 * Method to add a book, executed before every test case is run
	 */
	@Before
	public void initializeBooks() throws BookStoreException {
		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		booksToAdd.add(getDefaultBook());
		storeManager.addBooks(booksToAdd);
	}

	/**
	 * Method to clean up the book store, execute after every test case is run
	 */
	@After
	public void cleanupBooks() throws BookStoreException {
		storeManager.removeAllBooks();
	}

	/**
	 * Tests basic buyBook() functionality
	 */
	@Test
	public void testBuyAllCopiesDefaultBook() throws BookStoreException {
		// Set of books to buy
		Set<BookCopy> booksToBuy = new HashSet<BookCopy>();
		booksToBuy.add(new BookCopy(TEST_ISBN, NUM_COPIES));

		// Try to buy books
		client.buyBooks(booksToBuy);

		List<StockBook> listBooks = storeManager.getBooks();
		assertTrue(listBooks.size() == 1);
		StockBook bookInList = listBooks.get(0);
		StockBook addedBook = getDefaultBook();

		assertTrue(bookInList.getISBN() == addedBook.getISBN()
				&& bookInList.getTitle().equals(addedBook.getTitle())
				&& bookInList.getAuthor().equals(addedBook.getAuthor())
				&& bookInList.getPrice() == addedBook.getPrice()
				&& bookInList.getSaleMisses() == addedBook.getSaleMisses()
				&& bookInList.getAverageRating() == addedBook
						.getAverageRating()
				&& bookInList.getTimesRated() == addedBook.getTimesRated()
				&& bookInList.getTotalRating() == addedBook.getTotalRating()
				&& bookInList.isEditorPick() == addedBook.isEditorPick());

	}

	/**
	 * Tests that books with invalid ISBNs cannot be bought
	 */
	@Test
	public void testBuyInvalidISBN() throws BookStoreException {
		List<StockBook> booksInStorePreTest = storeManager.getBooks();

		// Try to buy a book with invalid isbn
		HashSet<BookCopy> booksToBuy = new HashSet<BookCopy>();
		booksToBuy.add(new BookCopy(TEST_ISBN, 1)); // valid
		booksToBuy.add(new BookCopy(-1, 1)); // invalid

		// Try to buy the books
		try {
			client.buyBooks(booksToBuy);
			fail();
		} catch (BookStoreException ex) {
			;
		}

		List<StockBook> booksInStorePostTest = storeManager.getBooks();
		// Check pre and post state are same
		assertTrue(booksInStorePreTest.containsAll(booksInStorePostTest)
				&& booksInStorePreTest.size() == booksInStorePostTest.size());

	}

	/**
	 * Tests that books can only be bought if they are in the book store
	 */
	@Test
	public void testBuyNonExistingISBN() throws BookStoreException {
		List<StockBook> booksInStorePreTest = storeManager.getBooks();

		// Try to buy a book with isbn which does not exist
		HashSet<BookCopy> booksToBuy = new HashSet<BookCopy>();
		booksToBuy.add(new BookCopy(TEST_ISBN, 1)); // valid
		booksToBuy.add(new BookCopy(100000, 10)); // invalid

		// Try to buy the books
		try {
			client.buyBooks(booksToBuy);
			fail();
		} catch (BookStoreException ex) {
			;
		}

		List<StockBook> booksInStorePostTest = storeManager.getBooks();
		// Check pre and post state are same
		assertTrue(booksInStorePreTest.containsAll(booksInStorePostTest)
				&& booksInStorePreTest.size() == booksInStorePostTest.size());

	}

	/**
	 * Tests that you can't buy more books than there are copies
	 */
	@Test
	public void testBuyTooManyBooks() throws BookStoreException {
		List<StockBook> booksInStorePreTest = storeManager.getBooks();

		// Try to buy more copies than there are in store
		HashSet<BookCopy> booksToBuy = new HashSet<BookCopy>();
		booksToBuy.add(new BookCopy(TEST_ISBN, NUM_COPIES + 1));

		try {
			client.buyBooks(booksToBuy);
			fail();
		} catch (BookStoreException ex) {
			;
		}

		List<StockBook> booksInStorePostTest = storeManager.getBooks();
		assertTrue(booksInStorePreTest.containsAll(booksInStorePostTest)
				&& booksInStorePreTest.size() == booksInStorePostTest.size());

	}

	/**
	 * Tests that you can't buy a negative number of books
	 */
	@Test
	public void testBuyNegativeNumberOfBookCopies() throws BookStoreException {
		List<StockBook> booksInStorePreTest = storeManager.getBooks();

		// Try to buy a negative number of copies
		HashSet<BookCopy> booksToBuy = new HashSet<BookCopy>();
		booksToBuy.add(new BookCopy(TEST_ISBN, -1));

		try {
			client.buyBooks(booksToBuy);
			fail();
		} catch (BookStoreException ex) {
			;
		}

		List<StockBook> booksInStorePostTest = storeManager.getBooks();
		assertTrue(booksInStorePreTest.containsAll(booksInStorePostTest)
				&& booksInStorePreTest.size() == booksInStorePostTest.size());

	}

	/**
	 * Tests that all books can be retrieved
	 */
	@Test
	public void testGetBooks() throws BookStoreException {
		Set<StockBook> booksAdded = new HashSet<StockBook>();
		booksAdded.add(getDefaultBook());

		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		booksToAdd.add(new ImmutableStockBook(TEST_ISBN + 1,
				"The Art of Computer Programming", "Donald Knuth", (float) 300,
				NUM_COPIES, 0, 0, 0, false));
		booksToAdd.add(new ImmutableStockBook(TEST_ISBN + 2,
				"The C Programming Language",
				"Dennis Ritchie and Brian Kerninghan", (float) 50, NUM_COPIES,
				0, 0, 0, false));

		booksAdded.addAll(booksToAdd);

		storeManager.addBooks(booksToAdd);

		// Get books in store
		List<StockBook> listBooks = storeManager.getBooks();

		// Make sure the lists equal each other
		assertTrue(listBooks.containsAll(booksAdded)
				&& listBooks.size() == booksAdded.size());
	}

	/**
	 * Tests that a list of books with a certain feature can be retrieved
	 */
	@Test
	public void testGetCertainBooks() throws BookStoreException {
		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		booksToAdd.add(new ImmutableStockBook(TEST_ISBN + 1,
				"The Art of Computer Programming", "Donald Knuth", (float) 300,
				NUM_COPIES, 0, 0, 0, false));
		booksToAdd.add(new ImmutableStockBook(TEST_ISBN + 2,
				"The C Programming Language",
				"Dennis Ritchie and Brian Kerninghan", (float) 50, NUM_COPIES,
				0, 0, 0, false));

		storeManager.addBooks(booksToAdd);

		// Get a list of ISBNs to retrieved
		Set<Integer> isbnList = new HashSet<Integer>();
		isbnList.add(TEST_ISBN + 1);
		isbnList.add(TEST_ISBN + 2);

		// Get books with that ISBN

		List<Book> books = client.getBooks(isbnList);
		// Make sure the lists equal each other
		assertTrue(books.containsAll(booksToAdd)
				&& books.size() == booksToAdd.size());

	}

	/**
	 * Tests that books cannot be retrieved if ISBN is invalid
	 */
	@Test
	public void testGetInvalidIsbn() throws BookStoreException {
		List<StockBook> booksInStorePreTest = storeManager.getBooks();

		// Make an invalid ISBN
		HashSet<Integer> isbnList = new HashSet<Integer>();
		isbnList.add(TEST_ISBN); // valid
		isbnList.add(-1); // invalid

		HashSet<BookCopy> booksToBuy = new HashSet<BookCopy>();
		booksToBuy.add(new BookCopy(TEST_ISBN, -1));

		try {
			client.getBooks(isbnList);
			fail();
		} catch (BookStoreException ex) {
			;
		}

		List<StockBook> booksInStorePostTest = storeManager.getBooks();
		assertTrue(booksInStorePreTest.containsAll(booksInStorePostTest)
				&& booksInStorePreTest.size() == booksInStorePostTest.size());


	}

/************************************************************************************/
	/**
	 * Here we want to test addBook functionality.
	 *
	 * 1. We create a BookStoreBook instance and add it to a list. Please note
	 * that we are also checking that the title and the author name could be any
	 * valid arbitrary string, which could also contain special characters.
	 *
	 * 2. We execute addBooks with the list creates in step 1 as a parameter.
	 *
	 * 3. Now we execute storeManager.getBooks, which returns a list of all the
	 * books available.
	 *
	 * 4. Finally we check whether the book added in step 2 is returned by
	 * getBooks. 5. We also try to add invalid books and check that they throw
	 * appropriate exceptions.
	 */
	@Test
	public void testAddBook() {
		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		Integer testISBN = 100;
		booksToAdd.add(new ImmutableStockBook(testISBN,
				"*@#&)%rdrrdjtIHH%^#)$&N 37874\n \t",
				"eurgqo	89274267^&#@&$%@&%$( \t", (float) 100, 5, 0, 0, 0,
				false));

		List<StockBook> listBooks = null;
		try {
			storeManager.addBooks(booksToAdd);
			listBooks = storeManager.getBooks();
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}

		Boolean containsTestISBN = false;
		Iterator<StockBook> it = listBooks.iterator();
		while (it.hasNext()) {
			Book b = it.next();
			if (b.getISBN() == testISBN)
				containsTestISBN = true;
		}
		assertTrue("List should contain the book added!", containsTestISBN);

		Boolean exceptionThrown = false;
		booksToAdd.add(new ImmutableStockBook(-1, "BookName", "Author",
				(float) 100, 5, 0, 0, 0, false));

		try {
			storeManager.addBooks(booksToAdd);
		} catch (BookStoreException e) {
			exceptionThrown = true;
		}
		assertTrue("Invlaid ISBN exception should be thrown!", exceptionThrown);
		List<StockBook> currentList = null;
		try {
			currentList = storeManager.getBooks();
			assertTrue(currentList.equals(listBooks));
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}

		exceptionThrown = false;
		booksToAdd.add(new ImmutableStockBook(testISBN + 1, "BookName",
				"Author", (float) 100, 0, 0, 0, 0, false));

		try {
			storeManager.addBooks(booksToAdd);
		} catch (BookStoreException e) {
			exceptionThrown = true;
		}
		assertTrue("Invalid number of copies exception should be thrown!",
				exceptionThrown);
		try {
			currentList = storeManager.getBooks();
			assertTrue(currentList.equals(listBooks));
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}

		exceptionThrown = false;
		booksToAdd.add(new ImmutableStockBook(testISBN + 2, "BookName",
				"Author", (float) -100, 0, 0, 0, 0, false));

		try {
			storeManager.addBooks(booksToAdd);
		} catch (BookStoreException e) {
			exceptionThrown = true;
		}
		assertTrue("Invlaid price exception should be thrown!", exceptionThrown);
		try {
			currentList = storeManager.getBooks();
			assertTrue(currentList.equals(listBooks));
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}

	}

	/**
	 * Here we want to test addCopies functionality. 1. We create a
	 * BookStoreBook instance with number of copies = 5.
	 *
	 * 2. We execute addBooks with the list creates in step 1 as a parameter.
	 *
	 * 3. Using storeManager.addCopies we add 2 more copies to the book added in
	 * step 2.
	 *
	 * 4. We execute storeManager.getBooks and check the number of copies for
	 * the book added in step 2.
	 *
	 * 5. Finally we test that the final number of copies = 7 (5 + 2).
	 *
	 * 6. We also test that if we try to add invalid number of copies
	 * /non-existing ISBN/invalid ISBN BookStoreException is thrown.
	 *
	 * 7. Finally we also test that all the invalid addCopies have not changed
	 * the initial status.
	 */
	@Test
	public void testAddCopies() {
		Integer testISBN = 200;
		Integer totalNumCopies = 7;

		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		booksToAdd.add(new ImmutableStockBook(testISBN, "Book Name",
				"Book Author", (float) 100, 5, 0, 0, 0, false));
		try {
			storeManager.addBooks(booksToAdd);
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}

		BookCopy bookCopy = new BookCopy(testISBN, 2);
		Set<BookCopy> bookCopyList = new HashSet<BookCopy>();
		bookCopyList.add(bookCopy);
		List<StockBook> listBooks = null;
		try {
			storeManager.addCopies(bookCopyList);
			listBooks = storeManager.getBooks();

			for (StockBook b : listBooks) {
				if (b.getISBN() == testISBN) {
					assertTrue("Number of copies!",
							b.getNumCopies() == totalNumCopies);
					break;
				}
			}
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}

		bookCopy = new BookCopy(testISBN, 0);
		Boolean invalidNumCopiesThrewException = false;
		bookCopyList = new HashSet<BookCopy>();
		bookCopyList.add(bookCopy);
		try {
			storeManager.addCopies(bookCopyList);
		} catch (BookStoreException e) {
			invalidNumCopiesThrewException = true;
		}
		assertTrue(invalidNumCopiesThrewException);
		List<StockBook> currentList = null;
		try {
			currentList = storeManager.getBooks();
			assertTrue(currentList.equals(listBooks));
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}

		bookCopy = new BookCopy(-1, 0);
		Boolean invalidISBNThrewException = false;
		bookCopyList = new HashSet<BookCopy>();
		bookCopyList.add(bookCopy);
		try {
			storeManager.addCopies(bookCopyList);
		} catch (BookStoreException e) {
			invalidISBNThrewException = true;
		}
		assertTrue(invalidISBNThrewException);
		try {
			currentList = storeManager.getBooks();
			assertTrue(currentList.equals(listBooks));
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}

		bookCopy = new BookCopy(1000000, 0);
		Boolean nonExistingISBNThrewException = false;
		bookCopyList = new HashSet<BookCopy>();
		bookCopyList.add(bookCopy);
		try {
			storeManager.addCopies(bookCopyList);
		} catch (BookStoreException e) {
			nonExistingISBNThrewException = true;
		}
		assertTrue(nonExistingISBNThrewException);
		try {
			currentList = storeManager.getBooks();
			assertTrue(currentList.equals(listBooks));
		} catch (BookStoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Here we test updateEditorsPick and getEditorPicks functionalities.
	 *
	 * 1. We create a book, make it an Editors pick by updateEditorsPick.
	 *
	 * 2. Execute getEditorPicks and check that the book is returned.
	 *
	 * 3. Now we remove this book from editor's pick.
	 *
	 * 4. Executing getEditorPicks should now throw exception.
	 *
	 * 5. We also test that exception is thrown if updateEditorsPick is executed
	 * with wrong arguments.
	 */
	@Test
	public void testUpdateEditorsPick() {
		Integer testISBN = 800;
		Set<StockBook> books = new HashSet<StockBook>();
		books.add(new ImmutableStockBook(testISBN, "Book Name", "Book Author",
				(float) 100, 1, 0, 0, 0, false));

		try {
			storeManager.addBooks(books);
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}

		Set<BookEditorPick> editorPicksVals = new HashSet<BookEditorPick>();
		BookEditorPick editorPick = new BookEditorPick(testISBN, true);
		editorPicksVals.add(editorPick);
		try {
			storeManager.updateEditorPicks(editorPicksVals);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		List<Book> lstEditorPicks = new ArrayList<Book>();
		try {
			lstEditorPicks = client.getEditorPicks(1);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		Boolean testISBNisInEditorPicks = false;
		for (Book book : lstEditorPicks) {
			if (book.getISBN() == testISBN)
				testISBNisInEditorPicks = true;
		}
		assertTrue("Chk if list contains testISBN!", testISBNisInEditorPicks);

		editorPicksVals.clear();
		editorPick = new BookEditorPick(testISBN, false);
		editorPicksVals.add(editorPick);
		try {
			storeManager.updateEditorPicks(editorPicksVals);
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}

		Boolean exceptionThrown = false;
		lstEditorPicks = new ArrayList<Book>();
		try {
			lstEditorPicks = client.getEditorPicks(1);
		} catch (BookStoreException e) {
			exceptionThrown = true;
		}

		assertFalse(exceptionThrown);

		exceptionThrown = false;
		editorPicksVals.clear();
		editorPick = new BookEditorPick(-1, false);
		editorPicksVals.add(editorPick);
		try {
			storeManager.updateEditorPicks(editorPicksVals);
		} catch (BookStoreException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);

		exceptionThrown = false;
		editorPicksVals.clear();
		editorPick = new BookEditorPick(1000000000, false);
		editorPicksVals.add(editorPick);
		try {
			storeManager.updateEditorPicks(editorPicksVals);
		} catch (BookStoreException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);

	}

	/**
	 * Here we want to test getBooksInDemand functionality
	 *
	 * 1. We create a book with 1 copy in stock.
	 *
	 * 2. We try to buy this book twice.
	 *
	 * 3. First time buying should be successful but the second time buying
	 * should throw exception book not in stock.
	 *
	 * 4. Now we execute storeManager.getBooksInDemand and check that testISBN
	 * is returned in this call.
	 *
	 * 5. Check that repeating step 1-3 for a new book will make
	 *  storeManager.getBooksInDemand return at least two books
	 *  including the one added last.
	 */

	@Test
	public void testGetBooksInDemand() {
		Integer testISBN = 500;


		// Get initial number of books in demand
		List<StockBook> booksInDemand = null;
		try {
			booksInDemand = storeManager.getBooksInDemand();
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}
		int initBooksInDemand = booksInDemand.size();


		// Add a book
		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		booksToAdd.add(new ImmutableStockBook(testISBN, "Book Name",
				"Book Author", (float) 100, 1, 0, 0, 0, false));
		try {
			storeManager.addBooks(booksToAdd);
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}

		// Buy it twice
		Set<BookCopy> booksToBuy = new HashSet<BookCopy>();
		booksToBuy.add(new BookCopy(testISBN, 1));
		try {
			client.buyBooks(booksToBuy);
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}
		Boolean notInStockExceptionThrown = false;
		try {
			client.buyBooks(booksToBuy);
		} catch (BookStoreException e) {
			notInStockExceptionThrown = true;
		}
		assertTrue("Trying to buy the book second time should throw exception",
				notInStockExceptionThrown);

		// The book should now be in demand
		booksInDemand = null;
		try {
			booksInDemand = storeManager.getBooksInDemand();
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}
		Boolean listContainsTestISBN = false;
		for (StockBook b : booksInDemand) {
			if (b.getISBN() == testISBN) {
				listContainsTestISBN = true;
				break;
			}
		}
		assertTrue("testISBN should be returned by getBooksInDemand",
				listContainsTestISBN);

		// Add another book
		booksToAdd = new HashSet<StockBook>();
		booksToAdd.add(new ImmutableStockBook(testISBN+1, "Book Name",
				"Book Author", (float) 100, 1, 0, 0, 0, false));
		try {
			storeManager.addBooks(booksToAdd);
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}

		// Buy it twice
		booksToBuy = new HashSet<BookCopy>();
		booksToBuy.add(new BookCopy(testISBN+1, 1));
		try {
			client.buyBooks(booksToBuy);
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}
		notInStockExceptionThrown = false;
		try {
			client.buyBooks(booksToBuy);
		} catch (BookStoreException e) {
			notInStockExceptionThrown = true;
		}
		assertTrue("Trying to buy the book second time should throw exception",
				notInStockExceptionThrown);

		// There should now be at least two books in demand
		// including the book just added
		booksInDemand = null;
		try {
			booksInDemand = storeManager.getBooksInDemand();
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}
		listContainsTestISBN = false;
		for (StockBook b : booksInDemand) {
			if (b.getISBN() == testISBN+1) {
				listContainsTestISBN = true;
				break;
			}
		}
		assertTrue("testISBN+1 should be returned by getBooksInDemand",
				listContainsTestISBN);
		assertTrue("there should be at least two books in demand",
				booksInDemand.size() == initBooksInDemand+2);

	}

	@AfterClass
	public static void tearDownAfterClass() throws BookStoreException {
		storeManager.removeAllBooks();
		if (!localTest) {
			((BookStoreHTTPProxy) client).stop();
			((StockManagerHTTPProxy) storeManager).stop();
		}
	}
}
