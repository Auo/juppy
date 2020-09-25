package com.auo.juppy.result;

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
    public String toString() {
        return "status: " + statusCode + "\n" +
                "runnerId: " + runnerId + "\n" +
                "responseTime: " + responseTime + "\n\n";
    }
}
