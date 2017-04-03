package io.runecache;

import java.util.Arrays;

public class Group {

    public final byte[][] childData;

    public Group(RuneBuffer data, int childCount) {
        data.offset = data.bytes.length - 1;
        int chunkCount = data.getUnsignedByte();
        int[][] chunkSizes = new int[chunkCount][childCount];
        int[] childArray = new int[childCount];
        int i;
        int j;
        int size;
        for (data.offset -= 1 + chunkCount * childCount * 4, i = 0, size = 0; i < chunkCount; i++, size = 0) {
            for (j = 0; j < childCount; j++) {
                size += data.getInteger();
                chunkSizes[i][j] = size;
                childArray[j] += size;
            }
        }
        childData = new byte[childCount][];
        for (i = 0; i < childCount; i++) {
            childData[i] = new byte[childArray[i]];
        }
        for (data.offset = 0, Arrays.fill(childArray, 0), i = 0; i < chunkCount; i++) {
            for (j = 0; j < childCount; childArray[j] += size, j++) {
                size = chunkSizes[i][j];
                byte[] chunkData = new byte[size];
                data.getBytes(chunkData);
                System.arraycopy(chunkData, 0, childData[j], childArray[j], size);
            }
        }
    }
}