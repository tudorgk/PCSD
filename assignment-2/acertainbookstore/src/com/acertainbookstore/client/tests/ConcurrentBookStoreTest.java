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
 * Test class to test the ConcurrentBookStore interface
 *
 */
public class ConcurrentBookStoreTest {

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
                ConcurrentCertainBookStore store = new ConcurrentCertainBookStore();
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

    /****************************************************************************/
    @Test
    public void addBuyTest() throws BookStoreException{

        int isbn1 = 11;
        int isbn2 = 12;
        int isbn3 = 13;

        //add books to the store with 5 copies each
        addBooks(isbn1,5);
        addBooks(isbn2,5);
        addBooks(isbn3,5);

        //create a book set that is added then bought
        BookCopy bookCopy1 = new BookCopy(isbn1, 10);
        BookCopy bookCopy2 = new BookCopy(isbn2, 10);
        BookCopy bookCopy3 = new BookCopy(isbn3, 10);
        HashSet<BookCopy> bookCopySet = new HashSet<BookCopy>();
        bookCopySet.add(bookCopy1);
        bookCopySet.add(bookCopy2);
        bookCopySet.add(bookCopy3);


        //create 2 clients. one for adding and one for buying.
        Thread client1 = new Thread (new ConcurrentAddCopies(bookCopySet));
        Thread client2 = new Thread (new ConcurrentBuyBooks(bookCopySet));

        // run
        client1.start();
        client2.start();

        // wait
        try {
            client1.join();
            client2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //the books after the adding and buying
        List<StockBook> storeManagerBooks = null;
        try {
            storeManagerBooks = storeManager.getBooks();
        } catch (BookStoreException e) {
            e.printStackTrace();
            fail();
        }
        // recount the stock
        Set<Integer> isbns = new HashSet<Integer>();
        isbns.add(isbn1);
        isbns.add(isbn2);
        isbns.add(isbn3);

        for (StockBook book : storeManagerBooks) {
            // first stock was 5
            if (isbns.contains(book.getISBN())) {
                assertTrue(5 == book.getNumCopies());
            }
        }
    }

    @Test
    public void complexAddBuyTest() throws BookStoreException{
        int tries = 5;

        int isbn1 = 21;
        int isbn2 = 22;
        int isbn3 = 23;

        BookCopy bookCopy1 = new BookCopy(isbn1, 2);
        BookCopy bookCopy2 = new BookCopy(isbn2, 2);
        BookCopy bookCopy3 = new BookCopy(isbn3, 2);
        Set<BookCopy> bookCopySet = new HashSet<BookCopy>();
        bookCopySet.add(bookCopy1);
        bookCopySet.add(bookCopy2);
        bookCopySet.add(bookCopy3);

        List<StockBook> before = null;
        List<StockBook> after = null;
        try {
            before = storeManager.getBooks();
            after = storeManager.getBooks();
            //add books to the store with 5 copies each
            addBooks(isbn1,5);
            addBooks(isbn2,5);
            addBooks(isbn3,5);
        } catch (BookStoreException e1) {
            e1.printStackTrace();
            fail();
        }

        //add the books to after list in order to keep track
        assertTrue(after.add(new ImmutableStockBook(isbn1, "Test of Thrones",
                "George RR Testin'", (float) 10, 5, 0, 0, 0, false)));
        assertTrue(after.add(new ImmutableStockBook(isbn2, "Test of Thrones",
                "George RR Testin'", (float) 10, 5, 0, 0, 0, false)));
        assertTrue(after.add(new ImmutableStockBook(isbn3, "Test of Thrones",
                "George RR Testin'", (float) 10, 5, 0, 0, 0, false)));

        Set<Integer> isbns = new HashSet<Integer>();
        isbns.add(isbn1);
        isbns.add(isbn2);
        isbns.add(isbn3);

        // create clients
        Thread getBooksClient = new Thread (new ConcurrentGetBooks(before, after, isbns, tries));
        Thread buyAndAddClient= new Thread (new ConcurrentBuyAndAdd(bookCopySet, tries));

        // run them
        getBooksClient.start();
        buyAndAddClient.start();

        // wait
        try {
            getBooksClient.join();
            buyAndAddClient.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void concurrentRateTest() throws BookStoreException{

        Integer isbn1 = 31;
        Integer isbn2 = 32;

        addBooks(isbn1,5);
        addBooks(isbn2, 5);

        BookRating rating1 = new BookRating(isbn1, 1);
        BookRating rating2 = new BookRating(isbn2, 5);

        Set<BookRating> ratings = new HashSet<BookRating>();
        ratings.add(rating1);
        ratings.add(rating2);

        // create clients
        Thread rater1 = new Thread (new ConcurrentRating(ratings, 2));
        Thread rater2 = new Thread (new ConcurrentRating(ratings, 2));

        // run threads
        rater1.start();
        rater2.start();

        // wait
        try {
            rater1.join();
            rater2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<StockBook> books = null;
        try {
            books = storeManager.getBooks();
        } catch (BookStoreException e) {
            e.printStackTrace();
            fail();
        }

        // expected rating:
        int expectedRating1 = 1 * (2 + 2);
        int expectedRating2 = 5 * (2 + 2);
        int expectedAvgRating1 = expectedRating1/ 4;
        int expectedAvgRating2 = expectedRating2 / 4;

        for (StockBook book : books) {
            if (book.getISBN() == isbn1) {
                assertTrue(book.getAverageRating() == expectedAvgRating1);
            }
            if (book.getISBN() == isbn2) {
                assertTrue(book.getAverageRating() == expectedAvgRating2);
            }
        }
    }

    @Test
    public void concurrentGetTopRated() throws BookStoreException{
        Integer isbn1 = 41;
        Integer isbn2 = 42;
        Integer isbn3 = 43;

        addBooks(isbn1,5);
        addBooks(isbn2,5);
        addBooks(isbn3,5);

        BookRating rating1 = new BookRating(isbn1, 2);
        BookRating rating2 = new BookRating(isbn2, 3);
        BookRating rating3 = new BookRating(isbn3, 4);


        Set<BookRating> ratings = new HashSet<BookRating>();
        ratings.add(rating1);
        ratings.add(rating2);
        ratings.add(rating3);

        // create clients
        Thread rater1 = new Thread (new ConcurrentRating(ratings, 10));
        Thread rater2 = new Thread (new ConcurrentRating(ratings, 3));
        
        // run threads
        rater1.start();
        rater2.start();

        // wait
        try {
            rater1.join();
            rater2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }

        List<Book> books = null;
        try {
            books = client.getTopRatedBooks(1);
        } catch (BookStoreException e) {
            e.printStackTrace();
            fail();
        }

        assertTrue(isbn3 == books.get(0).getISBN());
    }

    @AfterClass
    public static void tearDownAfterClass() throws BookStoreException {
        storeManager.removeAllBooks();
        if (!localTest) {
            ((BookStoreHTTPProxy) client).stop();
            ((StockManagerHTTPProxy) storeManager).stop();
        }
    }

    class ConcurrentAddCopies implements Runnable{
        HashSet<BookCopy> copies;
        public ConcurrentAddCopies (HashSet<BookCopy> bookCopies){
            this.copies = bookCopies;
        }

        @Override
        public void run() {
            try {
                storeManager.addCopies(copies);
            } catch (BookStoreException e) {
                e.printStackTrace();
                fail();
            }
        }
    }

    class ConcurrentBuyBooks implements Runnable{
        HashSet<BookCopy> books;
        public ConcurrentBuyBooks (HashSet<BookCopy> bookCopies){
            this.books = bookCopies;
        }

        @Override
        public void run() {
            try {
                client.buyBooks(books);
            } catch (BookStoreException e) {
                e.printStackTrace();
                fail();
            }
        }
    }

    class ConcurrentGetBooks implements Runnable{
        Set<Integer> isbns;
        int tries;
        List<StockBook> before;
        List<StockBook> after;
        public ConcurrentGetBooks (List<StockBook> before,
                                       List<StockBook> after,
                                       Set<Integer> ISBNList,
                                       int tries) {
            this.before = before;
            this.after  = after;
            this.isbns  = ISBNList;
            this.tries  = tries;
        }

        public void run() {
            List<StockBook> booksFromServer = null;
            for (int i=0; i < tries; i++)
                try {
                    booksFromServer = storeManager.getBooks();
                } catch (BookStoreException e) {
                    e.printStackTrace();
                    fail();
                }

            //check if the books are the same as in our before and after list
            if (! (compareBooks(booksFromServer, before)
                    || compareBooks(booksFromServer, after))) {
                fail();
            }
        }

        class compareByISBN implements Comparator<StockBook> {
            @Override
            public int compare(StockBook book0, StockBook book1) {
                return (int) (book1.getISBN() - book0.getISBN());
            }
        }

        public boolean compareBooks (List<StockBook> books1, List<StockBook> books2) {

            //sort the books by isbn
            Collections.sort(books1, new compareByISBN());
            Collections.sort(books2, new compareByISBN());

            //
            if (books1.size() != books2.size()) {
                return false;
            }
            for (int i = 0; i < books1.size(); i++) {
                if (! books1.get(i).equals(books2.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    class ConcurrentBuyAndAdd implements Runnable {
        Set<BookCopy> bookCopies;
        int tries;
        public ConcurrentBuyAndAdd (Set<BookCopy> bookCopies, int tries) {
            this.bookCopies = bookCopies;
            this.tries = tries;
        }
        public void run() {
            for (int i=0; i < tries; i++)
                try {
                    client.buyBooks(bookCopies);
                    storeManager.addCopies(bookCopies);
                } catch (BookStoreException e) {
                    e.printStackTrace();
                    fail();
                }
        }
    }

    class ConcurrentRating implements Runnable {
        Set<BookRating> ratings;
        int nrRatings;
        public ConcurrentRating (Set<BookRating> ratings, int numRatings) {
            this.ratings = ratings;
            this.nrRatings = numRatings;
        }
        public void run() {
            for (int i = 0; i < nrRatings; i++) {
                try {
                    client.rateBooks(ratings);
                } catch (BookStoreException e) {
                    e.printStackTrace();
                    fail();
                }
            }
        }
    }
}
