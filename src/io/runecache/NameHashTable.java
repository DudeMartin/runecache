package io.runecache;

import java.util.Arrays;

class NameHashTable {

    private final int[] table;

    NameHashTable(int[] hashes) {
        int size;
        for (size = 1; size <= hashes.length + (hashes.length >> 1); size <<= 1);
        table = new int[size + size];
        Arrays.fill(table, -1);
        for (int i = 0; i < hashes.length; i++) {
            int position;
            for (position = hashes[i] & size - 1; table[position + position + 1] != -1; position = position + 1 & size - 1);
            table[position + position] = hashes[i];
            table[position + position + 1] = i;
        }
    }

    int find(int hash) {
        int size = (table.length >> 1) - 1;
        int position = hash & size;
        while (true) {
            int current = table[position + position + 1];
            if (current == -1) {
                return -1;
            }
            if (table[position + position] == hash) {
                return current;
            }
            position = position + 1 & size;
        }
    }
}