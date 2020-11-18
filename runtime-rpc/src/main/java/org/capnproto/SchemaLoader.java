package org.capnproto;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

public class SchemaLoader {

    static Schema.CodeGeneratorRequest.Reader loadSchema(byte[] buffer) throws IOException {
        return loadSchema(ByteBuffer.wrap(buffer));
    }

    static Schema.CodeGeneratorRequest.Reader loadSchema(ByteBuffer buffer) throws IOException {
        var message = Serialize.read(buffer);
        return message.getRoot(Schema.CodeGeneratorRequest.factory);
    }

    static Schema.CodeGeneratorRequest.Reader loadSchema(Path path) throws IOException {
        try (var channel = (FileChannel)Files.newByteChannel(
                path, EnumSet.of(StandardOpenOption.READ))) {
            var mapped = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            if (mapped == null) {
                return null;
            }
            return loadSchema(mapped.asReadOnlyBuffer());
        }
    }
}