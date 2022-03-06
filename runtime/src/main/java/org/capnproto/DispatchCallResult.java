package org.capnproto;

import java.util.concurrent.CompletableFuture;

public final class DispatchCallResult {

    final CompletableFuture<java.lang.Void> promise;
    private final boolean streaming;

    public DispatchCallResult(CompletableFuture<java.lang.Void> promise, boolean isStreaming) {
        this.promise = promise;
        this.streaming = isStreaming;
    }

    public DispatchCallResult(Throwable exc) {
        this.promise = new CompletableFuture<>();
        this.streaming = false;
        this.promise.completeExceptionally(exc);
    }

    public boolean isStreaming() {
        return streaming;
    }
}
