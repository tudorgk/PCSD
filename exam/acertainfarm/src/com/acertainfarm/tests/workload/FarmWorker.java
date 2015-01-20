package com.acertainfarm.tests.workload;

import com.acertainfarm.constants.FarmConstants;
import com.acertainfarm.data.FieldState;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.acertainfarm.fieldstatus.interfaces.FieldStatus;

import java.awt.print.Book;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by tudorgk on 20/1/15.
 */
public class FarmWorker implements Callable<FarmWorkerRunResult>{

    private FarmWorkloadConfig configuration = null;
    private int numSuccessfulFrequentBookStoreInteraction = 0;
    private int numTotalFrequentBookStoreInteraction = 0;

    public FarmWorker(FarmWorkloadConfig config) {
        configuration = config;
    }


    private boolean runInteraction() {
        try{
            numTotalFrequentBookStoreInteraction++;
            runFrequentFarmClientInteraction();
            numSuccessfulFrequentBookStoreInteraction++;
        }catch (PrecisionFarmingException e) {
            e.printStackTrace();
            return false;
        }catch (AttributeOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public FarmWorkerRunResult call() throws Exception {
        int count = 1;
        long startTimeInNanoSecs = 0;
        long endTimeInNanoSecs = 0;
        int successfulInteractions = 0;
        long timeForRunsInNanoSecs = 0;

        Random rand = new Random();
        float chooseInteraction;

        // Perform the warmup runs
        while (count++ <= configuration.getWarmUpRuns()) {
            runInteraction();
        }

        count = 1;
        numTotalFrequentBookStoreInteraction = 0;
        numSuccessfulFrequentBookStoreInteraction = 0;

        // Perform the actual runs
        startTimeInNanoSecs = System.nanoTime();
        while (count++ <= configuration.getNumActualRuns()) {
            if (runInteraction()) {
                successfulInteractions++;
            }
        }
        endTimeInNanoSecs = System.nanoTime();
        timeForRunsInNanoSecs += (endTimeInNanoSecs - startTimeInNanoSecs);
        return new FarmWorkerRunResult(successfulInteractions,
                timeForRunsInNanoSecs, configuration.getNumActualRuns(),
                numSuccessfulFrequentBookStoreInteraction,
                numTotalFrequentBookStoreInteraction);
    }

    private void runFrequentFarmClientInteraction() throws PrecisionFarmingException,AttributeOutOfBoundsException {

        //sample ID list
        List<Integer> sampleList = new ArrayList<Integer>();
        for (int i =1; i<= FarmConstants.MAX_NO_FIELDS; i++){
            sampleList.add(i);
        }

        //get farm temp and humidity fo field list
        List<FieldState> fieldStatusList = configuration.getFieldStatus().query(configuration.getGenerator().sampleIDList(
                sampleList,configuration.getNumberOfFieldsToCheck()));

    }

}