package com.acertainfarm.sensoraggregator.interfaces;

import com.acertainfarm.data.Event;
import com.acertainfarm.sensoraggregator.sender.SenderRequest;
import com.acertainfarm.sensoraggregator.sender.SenderResult;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by tudorgk on 17/1/15.
 */
public interface Sender {
    public Future<SenderResult> sendUpdateWithPayload(String fieldUpdateServerAddress, SenderRequest request);
}
