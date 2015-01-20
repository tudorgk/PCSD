package com.acertainfarm.workload;

import com.acertainfarm.constants.FarmConstants;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.acertainfarm.fieldstatus.interfaces.FieldStatus;
import com.acertainfarm.utils.FarmUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tudorgk on 19/1/15.
 */
public class FarmClient extends Thread {

    protected FieldStatus fieldStatus = null;

    public FarmClient (FieldStatus fieldStatus){
        this.fieldStatus = fieldStatus;
    }

    @Override
    public void run() {

        try {
//            try sleeping the thread for 6 secs so that we have data in the field status
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        do{
        List<Integer> fieldIdList = new ArrayList<Integer>();

        for (int i=1; i<=FarmConstants.MAX_NO_FIELDS; i++){
            fieldIdList.add(i);
        }

        try {

            System.out.println(fieldStatus.query(fieldIdList));

        } catch (AttributeOutOfBoundsException e) {
            e.printStackTrace();
        } catch (PrecisionFarmingException e) {
            e.printStackTrace();
        }
        }while (true);
    }
}
