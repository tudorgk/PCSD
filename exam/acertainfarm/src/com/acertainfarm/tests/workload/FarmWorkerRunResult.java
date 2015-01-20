package com.acertainfarm.tests.workload;

/**
 * Created by tudorgk on 20/1/15.
 */
public class FarmWorkerRunResult {
    private int successfulInteractions; // total number of successful interactions
    private int totalRuns; // total number of interactions run
    private long elapsedTimeInNanoSecs; // total time taken to run all
    // interactions

    private int successfulFrequentFarmInteractionRuns;
    private int totalFrequentFarmInteractionRuns;



    public FarmWorkerRunResult(int successfulInteractions, long elapsedTimeInNanoSecs,int totalRuns,
                               int successfulFrequentFarmInteractionRuns, int totalFrequentFarmInteractionRuns) {
        this.successfulInteractions = successfulInteractions;
        this.totalRuns = totalRuns;
        this.elapsedTimeInNanoSecs = elapsedTimeInNanoSecs;
        this.successfulFrequentFarmInteractionRuns = successfulFrequentFarmInteractionRuns;
        this.totalFrequentFarmInteractionRuns = totalFrequentFarmInteractionRuns;
    }


    public int getSuccessfulFrequentFarmInteractionRuns() {
        return successfulFrequentFarmInteractionRuns;
    }

    public void setSuccessfulFrequentFarmInteractionRuns(int successfulFrequentFarmInteractionRuns) {
        this.successfulFrequentFarmInteractionRuns = successfulFrequentFarmInteractionRuns;
    }

    public int getTotalFrequentFarmInteractionRuns() {
        return totalFrequentFarmInteractionRuns;
    }

    public void setTotalFrequentFarmInteractionRuns(int totalFrequentFarmInteractionRuns) {
        this.totalFrequentFarmInteractionRuns = totalFrequentFarmInteractionRuns;
    }
    // runs

    public int getTotalRuns() {
        return totalRuns;
    }

    public void setTotalRuns(int totalRuns) {
        this.totalRuns = totalRuns;
    }

    public int getSuccessfulInteractions() {
        return successfulInteractions;
    }

    public void setSuccessfulInteractions(int successfulInteractions) {
        this.successfulInteractions = successfulInteractions;
    }

    public long getElapsedTimeInNanoSecs() {
        return elapsedTimeInNanoSecs;
    }

    public void setElapsedTimeInNanoSecs(long elapsedTimeInNanoSecs) {
        this.elapsedTimeInNanoSecs = elapsedTimeInNanoSecs;
    }



}
