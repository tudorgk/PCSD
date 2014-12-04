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
 * Test class to test the StockManager interface
 * 
 */
public class StockManagerTest {

	private static final Integer TEST_ISBN = 30345650;
	private static final Integer NUM_COPIES = 5;

	private static boolean localTest = true;
	private static StockManager storeManager;
	private static BookStore client;

	/**
	 * Initializes new instance
	 * 
	 * 
	 */
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
	 * Checks whether the insertion of a books with initialize books worked.
	 */
	@Test
	public void testInitializeBooks() throws BookStoreException {
		List<StockBook> addedBooks = new ArrayList<StockBook>();
		addedBooks.add(getDefaultBook());

		List<StockBook> listBooks = null;
		listBooks = storeManager.getBooks();

		assertTrue(addedBooks.containsAll(listBooks)
				&& addedBooks.size() == listBooks.size());
	}

	/**
	 * Checks whether an insertion of a books with an invalid ISBN is rejected
	 * 
	 * @throws BookStoreException
	 */
	@Test
	public void testaddBookInvalidISBN() throws BookStoreException {
		List<StockBook> booksInStorePreTest = storeManager.getBooks();

		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		booksToAdd.add(new ImmutableStockBook(TEST_ISBN + 1,
				"Harry Potter and Vivek", "JUnit Rowling", (float) 100, 5, 0,
				0, 0, false)); // valid
		booksToAdd.add(new ImmutableStockBook(-1, "Harry Potter and Marcos",
				"JUnit Rowling", (float) 100, 5, 0, 0, 0, false)); // invalid
																	// isbn

		try {
			storeManager.addBooks(booksToAdd);
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
	 * Checks whether the insertion of a book with a negative number of copies
	 * is rejected.
	 */
	@Test
	public void testAddBookInvalidCopies() throws BookStoreException {
		List<StockBook> booksInStorePreTest = storeManager.getBooks();

		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		booksToAdd.add(new ImmutableStockBook(TEST_ISBN + 1,
				"Harry Potter and Vivek", "JUnit Rowling", (float) 100, 5, 0,
				0, 0, false)); // valid
		booksToAdd.add(new ImmutableStockBook(TEST_ISBN + 2,
				"Harry Potter and Marcos", "JUnit Rowling", (float) 100, -1, 0,
				0, 0, false)); // invalid copies

		try {
			storeManager.addBooks(booksToAdd);
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
	 * Checks whether a book with negative price can be added
	 */
	@Test
	public void testAddBookInvalidPrice() throws BookStoreException {
		List<StockBook> booksInStorePreTest = storeManager.getBooks();

		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		booksToAdd.add(new ImmutableStockBook(TEST_ISBN + 1,
				"Harry Potter and Vivek", "JUnit Rowling", (float) 100, 5, 0,
				0, 0, false)); // valid
		booksToAdd.add(new ImmutableStockBook(TEST_ISBN + 2,
				"Harry Potter and Marcos", "JUnit Rowling", (float) -100, 5, 0,
				0, 0, false)); // invalid price

		try {
			storeManager.addBooks(booksToAdd);
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
	 * Tests adding copies of a book with correct parameters
	 */
	@Test
	public void testAddCopiesCorrectBook() throws BookStoreException {
		// Add a copy of a book
		int copies_to_add = 1;
		Set<BookCopy> bookCopiesSet = new HashSet<BookCopy>();
		bookCopiesSet.add(new BookCopy(TEST_ISBN, copies_to_add));

		storeManager.addCopies(bookCopiesSet);

		// Get books with that ISBN
		Set<Integer> testISBNList = new HashSet<Integer>();
		testISBNList.add(TEST_ISBN);
		List<StockBook> listBooks = storeManager.getBooksByISBN(testISBNList);
		assertTrue(listBooks.size() == 1);

		StockBook bookInList = listBooks.get(0);
		StockBook addedBook = getDefaultBook();

		assertTrue(bookInList.getNumCopies() == addedBook.getNumCopies()
				+ copies_to_add);

		// Painful hack since we want to check all fields except num copies on
		// an immutable object
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
	 * Checks whether the insertion of a negative number of copies with
	 * addCopies (N.B. not addBooks as above) is rejected
	 */
	@Test
	public void testAddCopiesInvalidNumCopies() throws BookStoreException {
		List<StockBook> booksInStorePreTest = storeManager.getBooks();

		Set<BookCopy> bookCopiesSet = new HashSet<BookCopy>();
		bookCopiesSet.add(new BookCopy(TEST_ISBN, -1));

		try {
			storeManager.addCopies(bookCopiesSet);
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
	 * Checks whether the insertion of a number of copies for an invalid ISBN
	 * with addCopies (N.B. not addBooks as above) is rejected
	 */
	@Test
	public void testAddCopiesInvalidISBN() throws BookStoreException {
		List<StockBook> booksInStorePreTest = storeManager.getBooks();

		Set<BookCopy> bookCopiesSet = new HashSet<BookCopy>();
		bookCopiesSet.add(new BookCopy(-1, NUM_COPIES));

		try {
			storeManager.addCopies(bookCopiesSet);
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
	 * Checks whether the insertion of a number of copies with an ISBN not in
	 * the system (N.B. not addBooks as above) is rejected
	 */
	@Test
	public void testAddCopiesNonExistingISBN() throws BookStoreException {
		List<StockBook> booksInStorePreTest = storeManager.getBooks();

		Set<BookCopy> bookCopiesSet = new HashSet<BookCopy>();
		bookCopiesSet.add(new BookCopy(TEST_ISBN, NUM_COPIES));
		bookCopiesSet.add(new BookCopy(TEST_ISBN + 1, NUM_COPIES));

		try {
			storeManager.addCopies(bookCopiesSet);
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
	 * Helper method to make an Editor's pick
	 */
	public void addEditorPick(int isbn, boolean pick) throws BookStoreException {
		Set<BookEditorPick> editorPicksVals = new HashSet<BookEditorPick>();
		BookEditorPick editorPick = new BookEditorPick(isbn, pick);
		editorPicksVals.add(editorPick);
		storeManager.updateEditorPicks(editorPicksVals);
	}

	/**
	 * Tests the basic editor pick functionality
	 */
	@Test
	public void testDefaultBookForEditorsPick() throws BookStoreException {

		// The default book should not be an editor pick
		List<Book> editorPicks = client.getEditorPicks(1);
		assertEquals(editorPicks.size(), 0);

		// Add an Editor's pick
		addEditorPick(TEST_ISBN, true);

		// Check it's there
		List<Book> editorPicksLists = client.getEditorPicks(1);
		assertTrue(editorPicksLists.size() == 1);

		Book defaultBookAdded = getDefaultBook();
		Book editorPick = editorPicksLists.get(0);

		assertTrue(editorPick.equals(defaultBookAdded));

	}

	/**
	 * Checks that a book can be removed
	 */
	@Test
	public void testRemoveBooks() throws BookStoreException {
		List<StockBook> booksAdded = new ArrayList<StockBook>();
		booksAdded.add(getDefaultBook());

		List<StockBook> booksInStoreList = storeManager.getBooks();
		assertTrue(booksInStoreList.equals(booksAdded));

		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		booksToAdd.add(new ImmutableStockBook(TEST_ISBN + 1,
				"The Art of Computer Programming", "Donald Knuth", (float) 300,
				NUM_COPIES, 0, 0, 0, false));
		booksToAdd.add(new ImmutableStockBook(TEST_ISBN + 2,
				"The C Programming Language",
				"Dennis Ritchie and Brian Kerninghan", (float) 50, NUM_COPIES,
				0, 0, 0, false));

		booksAdded.addAll(booksToAdd);

		// Add books in bookstore
		storeManager.addBooks(booksToAdd);
		booksInStoreList = storeManager.getBooks();
		assertTrue(booksInStoreList.containsAll(booksAdded)
				&& booksInStoreList.size() == booksAdded.size());

		Set<Integer> isbnSet = new HashSet<Integer>();
		isbnSet.add(TEST_ISBN);
		isbnSet.add(TEST_ISBN + 2);

		// Remove books with testISBN
		storeManager.removeBooks(isbnSet);

		booksAdded.remove(2);
		booksAdded.remove(0);

		// Check that testISBN was removed
		booksInStoreList = storeManager.getBooks();
		assertTrue(booksInStoreList.containsAll(booksAdded)
				&& booksInStoreList.size() == booksAdded.size());
	}

	/**
	 * Tests basic getBooksByISBN for the default book
	 */
	@Test
	public void testGetBooksByISBN() throws BookStoreException {
		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		booksToAdd.add(new ImmutableStockBook(TEST_ISBN + 1,
				"The Art of Computer Programming", "Donald Knuth", (float) 300,
				NUM_COPIES, 0, 0, 0, false));
		booksToAdd.add(new ImmutableStockBook(TEST_ISBN + 2,
				"The C Programming Language",
				"Dennis Ritchie and Brian Kerninghan", (float) 50, NUM_COPIES,
				0, 0, 0, false));
		storeManager.addBooks(booksToAdd);

		Set<Integer> isbnSet = new HashSet<Integer>();
		isbnSet.add(TEST_ISBN + 1);
		isbnSet.add(TEST_ISBN + 2);

		List<StockBook> listBooks = storeManager.getBooksByISBN(isbnSet);
		assertTrue(booksToAdd.containsAll(listBooks)
				&& booksToAdd.size() == listBooks.size());
	}

	/**
	 * Tests basic removeAllBooks functionality
	 */
	@Test
	public void testRemoveAllBooks() throws BookStoreException {
		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		booksToAdd.add(new ImmutableStockBook(TEST_ISBN + 1,
				"The Art of Computer Programming", "Donald Knuth", (float) 300,
				NUM_COPIES, 0, 0, 0, false));
		booksToAdd.add(new ImmutableStockBook(TEST_ISBN + 2,
				"The C Programming Language",
				"Dennis Ritchie and Brian Kerninghan", (float) 50, NUM_COPIES,
				0, 0, 0, false));
		storeManager.addBooks(booksToAdd);

		List<StockBook> booksInStoreList = storeManager.getBooks();
		assertTrue(booksInStoreList.size() == 3);

		storeManager.removeAllBooks();

		booksInStoreList = storeManager.getBooks();
		assertTrue(booksInStoreList.size() == 0);
	}

	/************************************************************************************/
	/**
	 *  addBook() Test
	 */
	@Test
	public void testAddBook() {
		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		Integer testISBN = 100;
		booksToAdd.add(new ImmutableStockBook(testISBN,
				"m=n86wz2CCFrqpWU2\nYs!}S",
				"pju%v_VVe=atN3mZD4\t \\z?8+", (float) 10, 5, 0, 0, 0,
				false));

		List<StockBook> listBooks = null;
		try {
			//test the add books
			storeManager.addBooks(booksToAdd);
			listBooks = storeManager.getBooks();
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}

		//check if the book exists
		Boolean containsTestISBN = false;
		Iterator<StockBook> it = listBooks.iterator();
		while (it.hasNext()) {
			Book b = it.next();
			if (b.getISBN() == testISBN)
				containsTestISBN = true;
		}
		assertTrue("Book added!", containsTestISBN);

		Boolean exceptionThrown = false;
		booksToAdd.add(new ImmutableStockBook(-1, "BookName", "Author",
				(float) 100, 5, 0, 0, 0, false));

		//test if the manager sends an exception for adding an invalid ISBN
		try {
			storeManager.addBooks(booksToAdd);
		} catch (BookStoreException e) {
			exceptionThrown = true;
		}

		assertTrue("Invlaid ISBN ", exceptionThrown);
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

		//check invalid number of copies
		try {
			storeManager.addBooks(booksToAdd);
		} catch (BookStoreException e) {
			exceptionThrown = true;
		}
		assertTrue("Invalid nr of copies!",
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

		//check invalid price
		try {
			storeManager.addBooks(booksToAdd);
		} catch (BookStoreException e) {
			exceptionThrown = true;
		}
		assertTrue("Invalid price!", exceptionThrown);
		try {
			currentList = storeManager.getBooks();
			assertTrue(currentList.equals(listBooks));
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}

	}

	/**
	 *
	 * addCopies() Test
	 */

	@Test
	public void testAddCopies() {
		Integer testISBN = 200;
		Integer totalNumCopies = 7;

		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		booksToAdd.add(new ImmutableStockBook(testISBN, "Name",
				"Author", (float) 100, 5, 0, 0, 0, false));
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

		//Test invalid number of copies
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

		//Invalid ISBN test
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

		//Non existing ISBN
		bookCopy = new BookCopy(99999999, 0);
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
	 * updateEditorsPick() and getEditorPicks() Tests
	 *
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
	 * getBooksInDemand() Test
	 *
	 * */

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
		assertTrue("Buy book second time!",
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
		assertTrue("testISBN is valid",
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
		assertTrue("buy book second time",
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
		assertTrue("testISBN+1 ",
				listContainsTestISBN);
		assertTrue("at least 2 books in demand",
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
