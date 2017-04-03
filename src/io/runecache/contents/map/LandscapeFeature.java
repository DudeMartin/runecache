package io.runecache.contents.map;

public class LandscapeFeature {

    public final int id;
    public final int localX;
    public final int localY;
    public final int height;
    public final int type;
    public final int orientation;

    LandscapeFeature(int id, int localX, int localY, int height, int type, int orientation) {
        this.id = id;
        this.localX = localX;
        this.localY = localY;
        this.height = height;
        this.type = type;
        this.orientation = orientation;
    }
}