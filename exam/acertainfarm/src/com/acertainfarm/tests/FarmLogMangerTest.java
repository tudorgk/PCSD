package com.acertainfarm.tests;


import com.acertainfarm.data.Event;
import com.acertainfarm.fieldstatus.log.FieldStatusLogManager;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tudorgk on 21/1/15.
 */
public class FarmLogMangerTest {

    public static FieldStatusLogManager logManager;

    @BeforeClass
    public static void setUpBeforeClass() throws IOException {
        logManager = new FieldStatusLogManager("field_status.log");
    }

    @Test
    public void writeToFileTest(){

        List<Event> list = new ArrayList<Event>();

        for (int i = 0; i<10; i++){
            list.add(new Event(1,12,12));
        }

        try {
            logManager.flushToFileLog("UPDATE", 1231, list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
