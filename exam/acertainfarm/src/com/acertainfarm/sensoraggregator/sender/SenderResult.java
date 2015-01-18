package com.acertainfarm.sensoraggregator.sender;

/**
 * Created by tudorgk on 18/1/15.
 */
public class SenderResult {
    private String serverAddress; //the server where the replication request was sent
    private boolean sendingSuccessful;

    public SenderResult(String serverAddress, boolean sendingSuccessful) {
        this.setServerAddress(serverAddress);
        this.setSendingSuccessful(sendingSuccessful);
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public boolean isSendingSuccessful() {
        return sendingSuccessful;
    }

    public void setSendingSuccessful(boolean sendingSuccessful) {
        this.sendingSuccessful = sendingSuccessful;
    }
}
