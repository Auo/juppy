package com.auo.juppy.result;

import java.util.Objects;
import java.util.UUID;

public class RunnerResult {
    public final UUID id;
    public final int statusCode;
    public final long responseTime;
    public final UUID runnerId;
    public final long timestamp;

    public RunnerResult(int statusCode, long responseTime, UUID runnerId, UUID id, long timestamp) {
        this.statusCode = statusCode;
        this.responseTime = responseTime;
        this.runnerId = runnerId;
        this.id = id;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunnerResult that = (RunnerResult) o;
        return statusCode == that.statusCode &&
                responseTime == that.responseTime &&
                timestamp == that.timestamp &&
                Objects.equals(runnerId, that.runnerId) &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode, responseTime, runnerId, timestamp, id);
    }

    @Override
    public String toString() {
        return  "id: " + id + "\n" +
                "timestamp: " + timestamp + "\n" +
                "statusCode: " + statusCode + "\n" +
                "runnerId: " + runnerId + "\n" +
                "responseTime: " + responseTime + "\n\n";
    }
}
