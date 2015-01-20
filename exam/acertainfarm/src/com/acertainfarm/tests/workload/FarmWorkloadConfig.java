package com.acertainfarm.tests.workload;

import com.acertainfarm.fieldstatus.interfaces.FieldStatus;
import com.acertainfarm.fieldstatus.server.FarmFieldStatus;

/**
 * Created by tudorgk on 19/1/15.
 */
public class FarmWorkloadConfig {
    private int numberOfFieldsToCheck = 8;
    private int warmUpRuns = 100;
    private int numActualRuns = 500;
    private FarmIDListGenerator generator = null;
    private FieldStatus fieldStatus = null;

    public void setNumberOfFieldsToCheck(int numberOfFieldsToCheck) {
        this.numberOfFieldsToCheck = numberOfFieldsToCheck;
    }

    public void setWarmUpRuns(int warmUpRuns) {
        this.warmUpRuns = warmUpRuns;
    }

    public void setNumActualRuns(int numActualRuns) {
        this.numActualRuns = numActualRuns;
    }

    public void setGenerator(FarmIDListGenerator generator) {
        this.generator = generator;
    }

    public void setFieldStatus(FarmFieldStatus fieldStatus) {
        this.fieldStatus = fieldStatus;
    }

    public int getNumberOfFieldsToCheck() {
        return numberOfFieldsToCheck;
    }

    public int getWarmUpRuns() {
        return warmUpRuns;
    }

    public int getNumActualRuns() {
        return numActualRuns;
    }

    public FarmIDListGenerator getGenerator() {
        return generator;
    }

    public FieldStatus getFieldStatus() {
        return fieldStatus;
    }

    public FarmWorkloadConfig (FieldStatus fieldStatus){
        this.fieldStatus = fieldStatus;
        this.generator = new FarmIDListGenerator();
    }
}
