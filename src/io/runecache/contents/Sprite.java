package io.runecache.contents;

import io.runecache.Cache;
import io.runecache.CacheConstants;
import io.runecache.ReferenceTable;
import io.runecache.RuneBuffer;

import java.awt.image.BufferedImage;
import java.io.IOException;

public final class Sprite {

    public static Sprite get(Cache cache, int spriteId) throws IOException {
        return new Sprite(cache.readUncompressedFile(CacheConstants.INDEX_SPRITES, spriteId));
    }

    public static Sprite[] get(Cache cache) throws IOException {
        ReferenceTable configurationsTable = new ReferenceTable(cache.readUncompressedFile(CacheConstants.INDEX_META, CacheConstants.INDEX_SPRITES));
        int spriteCount = configurationsTable.getEntryCount();
        Sprite[] sprites = new Sprite[spriteCount];
        for (int i = 0; i < spriteCount; i++) {
            sprites[i] = new Sprite(cache.readUncompressedFile(CacheConstants.INDEX_SPRITES, i));
        }
        return sprites;
    }

    private static final int FLAG_VERTICAL = 1;
    private static final int FLAG_ALPHA = 1 << 1;

    public BufferedImage[] frames;
    public int width;
    public int height;

    private Sprite(RuneBuffer data) {
        data.offset = data.bytes.length - 2;
        int frameCount = data.getShort();
        frames = new BufferedImage[frameCount];
        int[] cropX = new int[frameCount];
        int[] cropY = new int[frameCount];
        int[] cropWidths = new int[frameCount];
        int[] cropHeights = new int[frameCount];
        data.offset -= frameCount * 8 + 7;
        width = data.getShort();
        height = data.getShort();
        int paletteSize = data.getUnsignedByte() + 1;
        int[] palette = new int[paletteSize];
        int i;
        for (i = 0; i < frameCount; i++) {
            cropX[i] = data.getShort();
        }
        for (i = 0; i < frameCount; i++) {
            cropY[i] = data.getShort();
        }
        for (i = 0; i < frameCount; i++) {
            cropWidths[i] = data.getShort();
        }
        for (i = 0; i < frameCount; i++) {
            cropHeights[i] = data.getShort();
        }
        data.offset -= frameCount * 8 + 5 + (paletteSize - 1) * 3;
        for (i = 1; i < paletteSize; i++) {
            int color = data.getTriplet();
            palette[i] = (color == 0) ? 1 : color;
        }
        data.offset = 0;
        for (i = 0; i < frameCount; i++) {
            BufferedImage frameImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            frames[i] = frameImage;
            int flags = data.getUnsignedByte();
            int frameX = cropX[i], frameY = cropY[i], frameWidth = cropWidths[i], frameHeight = cropHeights[i];
            int[][] paletteIndices = new int[frameWidth][frameHeight];
            int x, y;
            if ((flags & FLAG_VERTICAL) != 0) {
                for (x = 0; x < frameWidth; x++) {
                    for (y = 0; y < frameHeight; y++) {
                        paletteIndices[x][y] = data.getUnsignedByte();
                    }
                }
            } else {
                for (y = 0; y < frameHeight; y++) {
                    for (x = 0; x < frameWidth; x++) {
                        paletteIndices[x][y] = data.getUnsignedByte();
                    }
                }
            }
            if ((flags & FLAG_ALPHA) != 0) {
                if ((flags & FLAG_VERTICAL) != 0) {
                    for (x = 0; x < frameWidth; x++) {
                        for (y = 0; y < frameHeight; y++) {
                            frameImage.setRGB(x + frameX, y + frameY, data.getUnsignedByte() << 24 | palette[paletteIndices[x][y]]);
                        }
                    }
                } else {
                    for (y = 0; y < frameHeight; y++) {
                        for (x = 0; x < frameWidth; x++) {
                            frameImage.setRGB(x + frameX, y + frameY, data.getUnsignedByte() << 24 | palette[paletteIndices[x][y]]);
                        }
                    }
                }
            } else {
                for (x = 0; x < frameWidth; x++) {
                    for (y = 0; y < frameHeight; y++) {
                        int index = paletteIndices[x][y];
                        if (index == 0) {
                            frameImage.setRGB(x + frameX, y + frameY, 0);
                        } else {
                            frameImage.setRGB(x + frameX, y + frameY, 0xFF000000 | palette[index]);
                        }
                    }
                }
            }
        }
    }
}