package io.runecache.tools;

import io.runecache.Cache;
import io.runecache.contents.ItemDefinition;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ItemDefinitionDumper {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting...");
        Cache cache = new Cache(Cache.locateBaseDirectory());
        Files.createDirectories(Paths.get("definitions"));
        ItemDefinition[]  definitions = ItemDefinition.get(cache);
        for (int i = 0; i < definitions.length; i++) {
            Files.write(Paths.get("definitions", i + ".json"), definitions[i].toString().getBytes());
        }
        System.out.println("Dumped " + definitions.length + " item definitions.");
    }
}