package org.capnproto;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

public interface AsyncByteListenChannel {
    public abstract <A> void accept(A attachment, CompletionHandler<AsyncByteChannel,? super A> handler);
}
