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

public class AnyStruct {

    public static final org.capnproto.StructSize STRUCT_SIZE = new org.capnproto.StructSize((short)0,(short)0);

    public static final class Factory extends org.capnproto.StructFactory<Builder, Reader> {
        public Factory() {
        }
        public final Reader constructReader(org.capnproto.SegmentReader segment, int data,int pointers, int dataSize, short pointerCount, int nestingLimit) {
            return new Reader(segment,data,pointers,dataSize,pointerCount,nestingLimit);
        }
        public final Builder constructBuilder(org.capnproto.SegmentBuilder segment, int data,int pointers, int dataSize, short pointerCount) {
            return new Builder(segment, data, pointers, dataSize, pointerCount);
        }
        public final org.capnproto.StructSize structSize() {
            return AnyStruct.STRUCT_SIZE;
        }
        public final Reader asReader(Builder builder) {
            return builder.asReader();
        }
    }

    public static final Factory factory = new Factory();

    public static final org.capnproto.StructList.Factory<Builder,Reader> listFactory =
            new org.capnproto.StructList.Factory<>(factory);

    public static final class Builder extends org.capnproto.StructBuilder {
        Builder(org.capnproto.SegmentBuilder segment, int data, int pointers,int dataSize, short pointerCount){
            super(segment, data, pointers, dataSize, pointerCount);
        }
        public final Reader asReader() {
            return new Reader(segment, data, pointers, dataSize, pointerCount, 0x7fffffff);
        }
    }

    public static final class Reader extends org.capnproto.StructReader {
        Reader(org.capnproto.SegmentReader segment, int data, int pointers,int dataSize, short pointerCount, int nestingLimit){
            super(segment, data, pointers, dataSize, pointerCount, nestingLimit);
        }

        public final <T> T getAs(StructReader.Factory<T> factory) {
            return factory.constructReader(this.segment, this.capTable, this.data, this.pointers, this.dataSize, this.pointerCount, this.nestingLimit);
        }
    }
}
