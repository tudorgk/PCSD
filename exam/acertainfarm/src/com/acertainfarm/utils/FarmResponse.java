package com.acertainfarm.utils;

import com.acertainfarm.data.FieldState;

import java.util.List;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmResponse {
    private Exception exception = null;
    private List<FieldState> result = null;

    public FarmResponse() {

    }

    public FarmResponse(Exception exception,
                             List<FieldState> result) {
        this.setException(exception);
        this.setResult(result);
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public List<FieldState> getResult() {
        return result;
    }

    public void setResult(List<FieldState> result) {
        this.result = result;
    }
}
