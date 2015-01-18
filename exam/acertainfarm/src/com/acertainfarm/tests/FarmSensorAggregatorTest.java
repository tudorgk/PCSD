package com.acertainfarm.tests;

import com.acertainfarm.sensoraggregator.server.FarmSensorAggregator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by tudorgk on 18/1/15.
 */
public class FarmSensorAggregatorTest {
    private static FarmSensorAggregator sensorAggregator;

    @BeforeClass
    public static void setUpBeforeClass() {
        try {
            sensorAggregator = new FarmSensorAggregator();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


//    @Before
//    public void initializeBooks() throws BookStoreException {
//        Set<StockBook> booksToAdd = new HashSet<StockBook>();
//        booksToAdd.add(getDefaultBook());
//        storeManager.addBooks(booksToAdd);
//    }
//
//    @After
//    public void cleanupBooks() throws BookStoreException {
//        storeManager.removeAllBooks();
//    }
//
//
//    @Test
//    public void testBuyInvalidISBN() throws BookStoreException {
//        List<StockBook> booksInStorePreTest = storeManager.getBooks();
//
//        // Try to buy a book with invalid isbn
//        HashSet<BookCopy> booksToBuy = new HashSet<BookCopy>();
//        booksToBuy.add(new BookCopy(TEST_ISBN, 1)); // valid
//        booksToBuy.add(new BookCopy(-1, 1)); // invalid
//
//        // Try to buy the books
//        try {
//            client.buyBooks(booksToBuy);
//            fail();
//        } catch (BookStoreException ex) {
//            ;
//        }
//
//        List<StockBook> booksInStorePostTest = storeManager.getBooks();
//        // Check pre and post state are same
//        assertTrue(booksInStorePreTest.containsAll(booksInStorePostTest)
//                && booksInStorePreTest.size() == booksInStorePostTest.size());
//
//    }
}
