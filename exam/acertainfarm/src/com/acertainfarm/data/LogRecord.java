package com.acertainfarm.data;

/**
 * Created by tudorgk on 19/1/15.
 */
public final class LogRecord {
    private final long logSN;
    private final String action;
    private final String transaction;

    @Override
    public String toString() {
        return  logSN + ";"
                + action + ";"
                + transaction + ";"
                + modified_object + ";"
                + '\n';
    }

    public long getLogSN() {
        return logSN;
    }

    public String getAction() {
        return action;
    }

    public String getTransaction() {
        return transaction;
    }

    public String getModified_object() {
        return modified_object;
    }

    private final String modified_object;

    public LogRecord(long logSN, String action, String transaction, String modified_object){
        this.logSN = logSN;
        this.action = action;
        this.transaction = transaction;
        this.modified_object = modified_object;
    }
}
