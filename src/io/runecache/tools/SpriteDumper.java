package io.runecache.tools;

import io.runecache.Cache;
import io.runecache.contents.Sprite;

import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SpriteDumper {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting...");
        Cache cache = new Cache(Cache.locateBaseDirectory());
        Path directoryPath = Paths.get("sprites");
        Files.createDirectories(directoryPath);
        Sprite[]  sprites = Sprite.get(cache);
        for (int i = 0; i < sprites.length; i++) {
            Sprite sprite = sprites[i];
            for (int j = 0; j < sprite.frames.length; j++) {
                ImageIO.write(sprite.frames[j], "png", Files.newOutputStream(directoryPath.resolve(i + "_" + j + ".png")));
            }
        }
        System.out.println("Dumped " + sprites.length + " sprites.");
    }
}