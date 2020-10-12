package com.auo.juppy.result;

import java.net.URI;

public class QueueItem {
    public final RunnerResult result;
    public final URI uri;

    public QueueItem(RunnerResult result, URI uri) {
        this.result = result;
        this.uri = uri;
    }
}
