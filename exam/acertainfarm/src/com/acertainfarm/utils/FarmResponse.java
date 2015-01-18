package com.acertainfarm.utils;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmResponse {
    private Exception exception = null;
    private FarmResult result = null;

    public FarmResponse() {

    }

    public FarmResponse(Exception exception,
                             FarmResult result) {
        this.setException(exception);
        this.setResult(result);
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public FarmResult getResult() {
        return result;
    }

    public void setResult(FarmResult result) {
        this.result = result;
    }
}
