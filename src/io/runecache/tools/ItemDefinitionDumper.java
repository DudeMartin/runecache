package io.runecache.tools;

import io.runecache.Cache;
import io.runecache.contents.ItemDefinition;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ItemDefinitionDumper {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting...");
        Cache cache = new Cache(Cache.locateBaseDirectory());
        Path directoryPath = Paths.get("definitions");
        Files.createDirectories(directoryPath);
        ItemDefinition[]  definitions = ItemDefinition.get(cache);
        for (int i = 0; i < definitions.length; i++) {
            Files.write(directoryPath.resolve(i + ".json"), definitions[i].toString().getBytes());
        }
        System.out.println("Dumped " + definitions.length + " item definitions.");
    }
}