// Copyright (c) 2013-2014 Sandstorm Development Group, Inc. and contributors
// Licensed under the MIT License:
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package org.capnproto;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class Text {
    public static final class Factory implements
                                      FromPointerReaderBlobDefault<Reader>,
                                      FromPointerBuilderBlobDefault<Builder>,
                                      PointerFactory<Builder, Reader>,
                                      SetPointerBuilder<Builder, Reader> {
        @Override
        public final Reader fromPointerReaderBlobDefault(SegmentReader segment, int pointer, java.nio.ByteBuffer defaultBuffer,
                                                   int defaultOffset, int defaultSize) {
            return WireHelpers.readTextPointer(segment, pointer, defaultBuffer, defaultOffset, defaultSize);
        }

        @Override
        public final Reader fromPointerReader(SegmentReader segment, CapTableReader capTable, int pointer, int nestingLimit) {
            return WireHelpers.readTextPointer(segment, pointer, null, 0, 0);
        }

        @Override
        public final Builder fromPointerBuilderBlobDefault(SegmentBuilder segment, CapTableBuilder capTable, int pointer,
                                                           java.nio.ByteBuffer defaultBuffer, int defaultOffset, int defaultSize) {
            return WireHelpers.getWritableTextPointer(pointer,
                                                      segment,
                                                      capTable,
                                                      defaultBuffer,
                                                      defaultOffset,
                                                      defaultSize);
        }

        @Override
        public final Builder fromPointerBuilder(SegmentBuilder segment, CapTableBuilder capTable, int pointer) {
            return WireHelpers.getWritableTextPointer(pointer,
                                                      segment,
                                                      capTable,
                                                      null, 0, 0);
        }

        @Override
        public Builder initFromPointerBuilder(SegmentBuilder segment, CapTableBuilder capTable, int pointer, int size) {
            return WireHelpers.initTextPointer(pointer, segment, capTable, size);
        }

        @Override
        public void setPointerBuilder(SegmentBuilder segment, CapTableBuilder capTable, int pointer, Reader value) {
            WireHelpers.setTextPointer(pointer, segment, capTable, value);
        }
    }
    public static final Factory factory = new Factory();

    public static final class Reader {
        public final ByteBuffer buffer;
        public final int offset; // in bytes
        public final int size; // in bytes, not including NUL terminator

        public Reader() {
            // TODO what about the null terminator?
            this.buffer = ByteBuffer.allocate(0);
            this.offset = 0;
            this.size = 0;
        }

        public Reader(ByteBuffer buffer, int offset, int size) {
            this.buffer = buffer;
            this.offset = offset * 8;
            this.size = size;
        }

        public Reader(String value) {
            byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            this.buffer = ByteBuffer.wrap(bytes);
            this.offset = 0;
            this.size = bytes.length;
        }

        public final int size() {
            return this.size;
        }

        public ByteBuffer asByteBuffer() {
            ByteBuffer dup = this.buffer.asReadOnlyBuffer();
            dup.position(this.offset);
            ByteBuffer result = dup.slice();
            result.limit(this.size);
            return result;
        }

        @Override
        public final String toString() {
            byte[] bytes = new byte[this.size];

            ByteBuffer dup = this.buffer.duplicate();
            dup.position(this.offset);
            dup.get(bytes, 0, this.size);

            return new String(bytes, StandardCharsets.UTF_8);
        }

    }

    public static final class Builder {
        public final ByteBuffer buffer;
        public final int offset; // in bytes
        public final int size; // in bytes

        public Builder() {
            this.buffer = ByteBuffer.allocate(0);
            this.offset = 0;
            this.size = 0;
        }

        public Builder(ByteBuffer buffer, int offset, int size) {
            this.buffer = buffer;
            this.offset = offset;
            this.size = size;
        }

        public ByteBuffer asByteBuffer() {
            ByteBuffer dup = this.buffer.duplicate();
            dup.position(this.offset);
            ByteBuffer result = dup.slice();
            result.limit(this.size);
            return result;
        }

        @Override
        public final String toString() {
            byte[] bytes = new byte[this.size];

            ByteBuffer dup = this.buffer.duplicate();
            dup.position(this.offset);
            dup.get(bytes, 0, this.size);

            return new String(bytes, StandardCharsets.UTF_8);
        }

    }

}
