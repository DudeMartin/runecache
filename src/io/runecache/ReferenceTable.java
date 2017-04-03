package io.runecache;

public class ReferenceTable {

    private final int[] ids;
    private final NameHashTable nameTable;
    private final int[] checksums;
    private final int[] versions;
    private final int[][] childIds;
    private final NameHashTable[] childNameTables;

    public ReferenceTable(RuneBuffer data) {
        int version = data.getUnsignedByte();
        if (version == 6) {
            data.skip(4);
        } else if (version != 5 && version != 7) {
            throw new CacheException("Bad version.");
        }
        int identifierFlag = data.getUnsignedByte();
        int entryCount = (identifierFlag >= 7) ? data.getSmartInteger() : data.getShort();
        ids = new int[entryCount];
        int i;
        int offset;
        int maximumId;
        for (i = 0, offset = 0, maximumId = -1; i < entryCount; i++) {
            ids[i] = offset += (identifierFlag >= 7) ? data.getSmartInteger() : data.getShort();
            maximumId = Math.max(maximumId, ids[i]);
        }
        maximumId++;
        checksums = new int[maximumId];
        versions = new int[maximumId];
        childIds = new int[maximumId][];
        if (identifierFlag != 0) {
            int[] nameHashes = new int[maximumId];
            for (i = 0; i < entryCount; i++) {
                nameHashes[ids[i]] = data.getInteger();
            }
            nameTable = new NameHashTable(nameHashes);
            childNameTables = new NameHashTable[maximumId];
        } else {
            nameTable = null;
            childNameTables = null;
        }
        for (i = 0; i < entryCount; i++) {
            checksums[ids[i]] = data.getInteger();
        }
        for (i = 0; i < entryCount; i++) {
            versions[ids[i]] = data.getInteger();
        }
        int[] childCounts = new int[maximumId];
        for (i = 0; i < entryCount; i++) {
            childCounts[ids[i]] = data.getShort();
        }
        int[] maximums = new int[maximumId];
        for (i = 0; i < entryCount; i++) {
            int id = ids[i];
            int childCount = childCounts[id];
            childIds[id] = new int[childCount];
            offset = 0;
            maximumId = -1;
            for (int j = 0; j < childCount; j++) {
                childIds[id][j] = offset;
                offset += (identifierFlag >= 7) ? data.getSmartInteger() : data.getShort();
                maximumId = Math.max(maximumId, childIds[id][j]);
            }
            maximums[id] = maximumId + 1;
        }
        if (identifierFlag != 0) {
            for (i = 0; i < entryCount; i++) {
                int id = ids[i];
                int childCount = childCounts[id];
                int[] childNameHashes = new int[maximums[id]];
                for (int j = 0; j < childCount; j++) {
                    childNameHashes[childIds[id][j]] = data.getInteger();
                }
                childNameTables[id] = new NameHashTable(childNameHashes);
            }
        }
    }

    public int getEntryCount() {
        return ids.length;
    }

    public int getGroupId(int entry) {
        return ids[entry];
    }

    public int getChildCount(int groupId) {
        return childIds[groupId].length;
    }

    public int getGroupId(String groupName) {
        return nameTable.find(hash(groupName.toLowerCase()));
    }

    public int getChildId(int groupId, String childName) {
        return childNameTables[groupId].find(hash(childName.toLowerCase()));
    }

    public int getChildId(String groupName, String childName) {
        int groupId = getGroupId(groupName);
        return getChildId(groupId, childName);
    }

    public int getChecksum(int groupId) {
        return checksums[groupId];
    }

    public int getVersion(int groupId) {
        return versions[groupId];
    }

    private static int hash(String string) {
        int result = 0;
        for (int i = 0; i < string.length(); i++) {
            result = (result << 5) - result + encodeCharacter(string.charAt(i));
        }
        return result;
    }

    private static byte encodeCharacter(char c) {
        switch (c) {
            case '\u20ac':
                return -128;
            case '\u201a':
                return -126;
            case '\u0192':
                return -125;
            case '\u201e':
                return -124;
            case '\u2026':
                return -123;
            case '\u2020':
                return -122;
            case '\u2021':
                return -121;
            case '\u02c6':
                return -120;
            case '\u2030':
                return -119;
            case '\u0160':
                return -118;
            case '\u2039':
                return -117;
            case '\u0152':
                return -116;
            case '\u017d':
                return -114;
            case '\u2018':
                return -111;
            case '\u2019':
                return -110;
            case '\u201c':
                return -109;
            case '\u201d':
                return -108;
            case '\u2022':
                return -107;
            case '\u2013':
                return -106;
            case '\u2014':
                return -105;
            case '\u02dc':
                return -104;
            case '\u2122':
                return -103;
            case '\u0161':
                return -102;
            case '\u203a':
                return -101;
            case '\u0153':
                return -100;
            case '\u017e':
                return -98;
            case '\u0178':
                return -97;
            default:
                return (byte) c;
        }
    }
}