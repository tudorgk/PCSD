package com.acertainfarm.utils;

import com.acertainfarm.exceptions.AttributeOutOfBoundsException;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmResponse {
    private AttributeOutOfBoundsException exception = null;
    private FarmResult result = null;

    public FarmResponse() {

    }

    public FarmResponse(AttributeOutOfBoundsException exception,
                             FarmResult result) {
        this.setException(exception);
        this.setResult(result);
    }

    public AttributeOutOfBoundsException getException() {
        return exception;
    }

    public void setException(AttributeOutOfBoundsException exception) {
        this.exception = exception;
    }

    public FarmResult getResult() {
        return result;
    }

    public void setResult(FarmResult result) {
        this.result = result;
    }
}
