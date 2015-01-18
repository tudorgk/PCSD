package com.acertainfarm.sensoraggregator.sender;

import com.acertainfarm.utils.FarmMessageTag;

import java.util.Map;


/**
 * Created by tudorgk on 18/1/15.
 */
public class SenderRequest {
    private Map<?,?> payload;
    private FarmMessageTag messageType;

    public SenderRequest(Map<?,?> payload, FarmMessageTag messageType) {
        this.setPayload(payload);
        this.setMessageType(messageType);
    }

    public void setPayload(Map <?,?> payload){
        this.payload = payload;
    }

    public Map<?,?> getPayLoad(){
        return payload;
    }

    public FarmMessageTag getMessageType() {
        return messageType;
    }

    public void setMessageType(FarmMessageTag messageType) {
        this.messageType = messageType;
    }

}
