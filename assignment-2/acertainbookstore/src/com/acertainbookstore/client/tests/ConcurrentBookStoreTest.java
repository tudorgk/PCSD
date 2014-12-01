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
    

    @AfterClass
    public static void tearDownAfterClass() throws BookStoreException {
        storeManager.removeAllBooks();
        if (!localTest) {
            ((BookStoreHTTPProxy) client).stop();
            ((StockManagerHTTPProxy) storeManager).stop();
        }
    }
}
