package org.capnproto;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

public class AsyncSocketByteAdapter implements AsyncByteChannel {
    private final AsynchronousSocketChannel socketChannel;

    public AsyncSocketByteAdapter(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public <A> void read(ByteBuffer dst, long timeout, TimeUnit unit, A attachment, CompletionHandler<Integer, A> handler) {
        this.socketChannel.read(dst, timeout, unit, attachment, handler);
    }

    @Override
    public <A> void write(ByteBuffer[] srcs, int offset, int length, long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, ? super A> handler) {
        this.socketChannel.write(srcs, offset, length, timeout, unit, attachment, handler);
    }

    @Override
    public void close() throws IOException {
        this.socketChannel.close();
    }
}
