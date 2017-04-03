package io.runecache;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

public class Cache implements Closeable {

    private static final int INDEX_TABLE_SIZE = 256;
    private static final int INDEX_ENTRY_SIZE = 6;
    private static final int DATA_BLOCK_SIZE = 520;
    private static final int DATA_BLOCK_HEADER_SIZE = 8;
    private static final int MAXIMUM_DATA_SIZE = DATA_BLOCK_SIZE - DATA_BLOCK_HEADER_SIZE;
    private static final int[] NULL_XTEA_KEY = new int[4];
    private static final int NO_COMPRESSION = 0;
    private static final int BZIP2_COMPRESSION = 1;
    private static final int GZIP_COMPRESSION = 2;
    private static final byte[] BZIP2_HEADER = new byte[] { 'B', 'Z', 'h', '1'};

    private final RandomAccessFile[] indices;
    private final RandomAccessFile data;

    public Cache(Path base) throws FileNotFoundException {
        if (base == null || !Files.exists(base)) {
            throw new IllegalArgumentException("The base directory does not exist.");
        }
        indices = new RandomAccessFile[INDEX_TABLE_SIZE];
        boolean empty = true;
        for (int i = 0; i < INDEX_TABLE_SIZE; i++) {
            Path indexPath = base.resolve("main_file_cache.idx" + i);
            if (Files.isRegularFile(indexPath)) {
                indices[i] = new RandomAccessFile(indexPath.toFile(), "rw");
                empty = false;
            }
        }
        if (empty) {
            throw new FileNotFoundException("No valid index files in the cache directory.");
        }
        Path dataPath = base.resolve("main_file_cache.dat2");
        if (Files.isRegularFile(dataPath)) {
            data = new RandomAccessFile(dataPath.toFile(), "rw");
        } else {
            throw new FileNotFoundException("No valid data file.");
        }
    }

    public RuneBuffer readFile(int index, int entry) throws IOException {
        RandomAccessFile indexFile = indices[index];
        int entryOffset = entry * INDEX_ENTRY_SIZE;
        if (indexFile.length() < entryOffset + INDEX_ENTRY_SIZE) {
            throw new CacheException("Bad entry index.");
        }
        RuneBuffer locationData = new RuneBuffer(DATA_BLOCK_HEADER_SIZE);
        indexFile.seek(entryOffset);
        indexFile.readFully(locationData.bytes, 0, INDEX_ENTRY_SIZE);
        int fileSize = locationData.getTriplet();
        if (fileSize < 0) {
            throw new CacheException("Bad file size.");
        }
        int blockIndex = locationData.getTriplet();
        if (blockIndex <= 0 || blockIndex > data.length() / DATA_BLOCK_SIZE) {
            throw new CacheException("Bad block index.");
        }
        RuneBuffer fileData = new RuneBuffer(fileSize);
        for (int bytesRead = 0, toRead; bytesRead < fileSize; bytesRead += toRead) {
            data.seek(blockIndex * DATA_BLOCK_SIZE);
            data.readFully(locationData.bytes);
            locationData.offset = 0;
            int nextEntry = locationData.getShort();
            if (nextEntry != entry) {
                throw new CacheException("Entry mismatch.");
            }
            int currentBlock = locationData.getShort();
            if (currentBlock != bytesRead / MAXIMUM_DATA_SIZE) {
                throw new CacheException("Block number mismatch.");
            }
            blockIndex = locationData.getTriplet();
            int nextIndex = locationData.getUnsignedByte();
            if (nextIndex != index) {
                throw new CacheException("Index mismatch.");
            }
            toRead = Math.min(fileSize - bytesRead, MAXIMUM_DATA_SIZE);
            data.readFully(fileData.bytes, bytesRead, toRead);
        }
        return fileData;
    }

    public RuneBuffer uncompressFileContents(RuneBuffer data, int[] xtea) throws IOException {
        int compression = data.getUnsignedByte();
        int size = data.getInteger();
        if (size < 0 || size > data.bytes.length) {
            throw new CacheException("Bad size.");
        }
        if (xtea != null) {
            if (xtea.length != 4) {
                throw new IllegalArgumentException("The XTEA key must be 128 bits.");
            } else if (Arrays.equals(xtea, NULL_XTEA_KEY)) {
                throw new IllegalArgumentException("The XTEA key cannot be zero.");
            }
            data.xteaDecipher(xtea, 5, size + (compression == NO_COMPRESSION ? 5 : 9));
        }
        byte[] contents;
        if (compression == NO_COMPRESSION) {
            contents = new byte[size];
            data.getBytes(contents);
        } else {
            int uncompressedSize = data.getInteger();
            contents = new byte[uncompressedSize];
            InputStream compressedStream;
            if (compression == BZIP2_COMPRESSION) {
                compressedStream = new BZip2CompressorInputStream(new SequenceInputStream(new ByteArrayInputStream(BZIP2_HEADER), new ByteArrayInputStream(data.bytes, 9, size)));
            } else if (compression == GZIP_COMPRESSION) {
                compressedStream = new GZIPInputStream(new ByteArrayInputStream(data.bytes, 9, size));
            } else {
                throw new CacheException("Bad compression type.");
            }
            try {
                for (int bytesRead = 0; bytesRead < uncompressedSize; bytesRead += compressedStream.read(contents, bytesRead, uncompressedSize - bytesRead));
            } finally {
                compressedStream.close();
            }
        }
        return new RuneBuffer(contents);
    }

    public RuneBuffer uncompressFileContents(RuneBuffer data) throws IOException {
        return uncompressFileContents(data, null);
    }

    public RuneBuffer readUncompressedFile(int index, int entry) throws IOException {
        return uncompressFileContents(readFile(index, entry));
    }

    @Override
    public void close() throws IOException {
        try {
            for (RandomAccessFile indexFile : indices) {
                if (indexFile != null) {
                    try {
                        indexFile.close();
                    } catch (IOException ignored) {}
                }
            }
        } finally {
            data.close();
        }
    }

    public static Path locateBaseDirectory() {
        Path base = Paths.get("cache");
        if (Files.exists(base)) {
            return base;
        }
        String homePath = System.getProperty("user.home");
        if (homePath == null) {
            homePath = System.getProperty("os.name").contains("indow") ? System.getenv("USERPROFILE") : System.getenv("HOME");
        }
        base = Paths.get(homePath, "jagexcache", "oldschool", "LIVE");
        if (Files.exists(base)) {
            return base;
        }
        return null;
    }
}