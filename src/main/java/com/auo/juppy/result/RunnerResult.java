package com.auo.juppy.result;

import java.util.Objects;
import java.util.UUID;

public class RunnerResult {
    public final int statusCode;
    public final long responseTime;
    public final UUID runnerId;

    public RunnerResult(int statusCode, long responseTime, UUID runnerId) {
        this.statusCode = statusCode;
        this.responseTime = responseTime;
        this.runnerId = runnerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunnerResult that = (RunnerResult) o;
        return statusCode == that.statusCode &&
                responseTime == that.responseTime &&
                Objects.equals(runnerId, that.runnerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode, responseTime, runnerId);
    }

    @Override
    public String toString() {
        return "status: " + statusCode + "\n" +
                "runnerId: " + runnerId + "\n" +
                "responseTime: " + responseTime + "\n\n";
    }
}
