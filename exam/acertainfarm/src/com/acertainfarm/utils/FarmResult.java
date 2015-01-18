package com.acertainfarm.utils;

import java.util.List;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmResult {
    private List<?> resultList;
    private long snapshotId;

    public FarmResult(List<?> resultList, long snapshotId) {
        this.setResultList(resultList);
        this.setSnapshotId(snapshotId);
    }

    public List<?> getResultList() {
        return resultList;
    }

    public void setResultList(List<?> resultList) {
        this.resultList = resultList;
    }

    public long getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(long snapshotId) {
        this.snapshotId = snapshotId;
    }
}
