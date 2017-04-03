package io.runecache.contents.map;

import io.runecache.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapChunk {

    public static MapChunk read(Cache cache, int mapId, int[] xtea) throws IOException {
        ReferenceTable landscapeTable = new ReferenceTable(cache.readUncompressedFile(CacheConstants.INDEX_META, CacheConstants.INDEX_LANDSCAPES));
        int chunkX = (mapId >> 8) & 0xFF;
        int chunkY = mapId & 0xFF;
        int mapFileId = landscapeTable.getGroupId("m" + chunkX + "_" + chunkY);
        int landscapeFileId = landscapeTable.getGroupId("l" + chunkX + "_" + chunkY);
        if (mapFileId == -1 || landscapeFileId == -1) {
            throw new CacheException("Bad map data ID.");
        }
        RuneBuffer mapData = cache.readUncompressedFile(CacheConstants.INDEX_LANDSCAPES, mapFileId);
        RuneBuffer landscapeData = cache.uncompressFileContents(cache.readFile(CacheConstants.INDEX_LANDSCAPES, landscapeFileId), xtea);
        return new MapChunk(chunkX, chunkY, mapData, landscapeData);
    }

    public final int chunkX;
    public final int chunkY;
    public final int[][][] tileHeights = new int[4][64][64];
    public final byte[][][] overlayIds = new byte[4][64][64];
    public final byte[][][] overlayPaths = new byte[4][64][64];
    public final byte[][][] overlayRotations = new byte[4][64][64];
    public final byte[][][] renderRules = new byte[4][64][64];
    public final byte[][][] underlayIds = new byte[4][64][64];
    public final List<LandscapeFeature> features = new ArrayList<>();

    private MapChunk(int chunkX, int chunkY, RuneBuffer mapData, RuneBuffer landscapeData) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        readMapData(mapData);
        readLandscapeData(landscapeData);
    }

    private void readMapData(RuneBuffer data) {
        for (int z = 0; z < 4; z++) {
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++) {
                    while (true) {
                        int attribute = data.getUnsignedByte();
                        if (attribute == 0) {
                            tileHeights[z][x][y] = (z == 0) ? HeightUtilities.calculateTileHeight(x, y, chunkX << 6, chunkY << 6) : tileHeights[z - 1][x][y] - 240;
                            break;
                        } else if (attribute == 1) {
                            int height = data.getUnsignedByte();
                            if (height == 1) {
                                height = 0;
                            }
                            tileHeights[z][x][y] = (z == 0) ? (-height * 8) : (tileHeights[z - 1][x][y] - height * 8);
                            break;
                        } else if (attribute <= 49) {
                            overlayIds[z][x][y] = (byte) data.getByte();
                            overlayPaths[z][x][y] = (byte) ((attribute - 2) / 4);
                            overlayRotations[z][x][y] = (byte) (attribute - 2 & 0x3);
                        } else if (attribute <= 81) {
                            renderRules[z][x][y] = (byte) (attribute - 49);
                        } else {
                            underlayIds[z][x][y] = (byte) (attribute - 81);
                        }
                    }
                }
            }
        }
    }

    private void readLandscapeData(RuneBuffer data) {
        int id = -1;
        int offset;
        while ((offset = data.getUnsignedSmartShort()) != 0) {
            id += offset;
            int position = 0;
            while ((offset = data.getUnsignedSmartShort()) != 0) {
                position += offset - 1;
                int localY = position & 0x3F;
                int localX = position >> 6 & 0x3F;
                int height = position >> 12;
                int attributes = data.getUnsignedByte();
                int type = attributes >> 2;
                int orientation = attributes & 0x3;
                features.add(new LandscapeFeature(id, localX, localY, height, type, orientation));
            }
        }
    }
}