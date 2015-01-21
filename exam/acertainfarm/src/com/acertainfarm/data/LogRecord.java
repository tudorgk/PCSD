package com.acertainfarm.data;

/**
 * Created by tudorgk on 19/1/15.
 */
public final class LogRecord {
    private final long logSN;
    private final String action;
    private final long timePeriod;

    @Override
    public String toString() {
        return  logSN + ";"
                + timePeriod + ";"
                + action + ";"
                + modified_object 
                + '\n';
    }

    public long getLogSN() {
        return logSN;
    }

    public String getAction() {
        return action;
    }

    public long getTimePeriod() {
        return timePeriod;
    }

    public String getModified_object() {
        return modified_object;
    }

    private final String modified_object;

    public LogRecord(long logSN, String action, long timePeriod, String modified_object){
        this.logSN = logSN;
        this.action = action;
        this.timePeriod = timePeriod;
        this.modified_object = modified_object;
    }
}
