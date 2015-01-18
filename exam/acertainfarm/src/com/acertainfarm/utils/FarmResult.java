package com.acertainfarm.utils;

import java.util.Map;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmResult {
    private Map<?,?> resultMap;
    private long snapshotId;

    public FarmResult(Map<?,?> resultMap, long snapshotId) {
        this.setResultMap(resultMap);
        this.setSnapshotId(snapshotId);
    }

    public Map<?,?> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<?,?>resultMap) {
        this.resultMap = resultMap;
    }

    public long getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(long snapshotId) {
        this.snapshotId = snapshotId;
    }
}
