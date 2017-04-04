package io.runecache.contents;

import io.runecache.*;
import org.json.JSONObject;

import java.io.IOException;

public final class ItemDefinition {

    public static ItemDefinition get(Cache cache, int itemId) throws IOException {
        ReferenceTable configurationsTable = new ReferenceTable(cache.readUncompressedFile(CacheConstants.INDEX_META, CacheConstants.INDEX_CONFIGURATIONS));
        RuneBuffer itemData = cache.readUncompressedFile(CacheConstants.INDEX_CONFIGURATIONS, CacheConstants.GROUP_CONFIGURATIONS_ITEMS);
        Group itemGroup = new Group(itemData, configurationsTable.getChildCount(CacheConstants.GROUP_CONFIGURATIONS_ITEMS));
        return new ItemDefinition(new RuneBuffer(itemGroup.childData[itemId]));
    }

    public static ItemDefinition[] get(Cache cache) throws IOException {
        ReferenceTable configurationsTable = new ReferenceTable(cache.readUncompressedFile(CacheConstants.INDEX_META, CacheConstants.INDEX_CONFIGURATIONS));
        RuneBuffer itemData = cache.readUncompressedFile(CacheConstants.INDEX_CONFIGURATIONS, CacheConstants.GROUP_CONFIGURATIONS_ITEMS);
        Group itemGroup = new Group(itemData, configurationsTable.getChildCount(CacheConstants.GROUP_CONFIGURATIONS_ITEMS));
        ItemDefinition[] definitions = new ItemDefinition[itemGroup.childData.length];
        for (int i = 0; i < itemGroup.childData.length; i++) {
            definitions[i] = new ItemDefinition(new RuneBuffer(itemGroup.childData[i]));
        }
        return definitions;
    }

    public int inventoryModel;
    public String name;
    public int rotationLength;
    public int rotateX;
    public int rotateY;
    public int translateX;
    public int translateY;
    public boolean stackable;
    public int cost = 1;
    public boolean members;
    public int modelMale1 = -1;
    public int modelMaleTranslateY;
    public int modelMale2 = -1;
    public int modelFemale1 = -1;
    public int modelFemaleTranslateY;
    public int modelFemale2 = -1;
    public String[] groundActions = new String[] { null, null, "Take", null, null };
    public String[] interfaceOptions = new String[] { null, null, null, null, "Drop" };
    public short[] modelRecolorFind;
    public short[] modelRecolorReplace;
    public short[] textureFind;
    public short[] textureReplace;
    public boolean tradable;
    public int modelMale3 = -1;
    public int modelFemale3 = -1;
    public int maleHeadModel1 = -1;
    public int femaleHeadModel1 = -1;
    public int maleHeadModel2 = -1;
    public int femaleHeadModel2 = -1;
    public int rotateZ;
    public int noteItemId = -1;
    public int noteTemplateId = -1;
    public int[] stackVariantId;
    public int[] stackVariantSize;
    public int modelScaleX = 128;
    public int modelScaleY = 128;
    public int modelScaleZ = 128;
    public int lightIntensity;
    public int lightMagnitude;
    public int teamNumber;

    private ItemDefinition(RuneBuffer data) {
        while (true) {
            int opcode = data.getUnsignedByte();
            switch (opcode) {
                case 0:
                    return;
                case 1:
                    inventoryModel = data.getShort();
                    break;
                case 2:
                    name = data.getString();
                    break;
                case 4:
                    rotationLength = data.getShort();
                    break;
                case 5:
                    rotateX = data.getShort();
                    break;
                case 6:
                    rotateY = data.getShort();
                    break;
                case 7:
                    translateX = data.getShort();
                    if (translateX > Short.MAX_VALUE) {
                        translateX -= 0x10000;
                    }
                    break;
                case 8:
                    translateY = data.getShort();
                    if (translateY > Short.MAX_VALUE) {
                        translateY -= 0x10000;
                    }
                    break;
                case 11:
                    stackable = true;
                    break;
                case 12:
                    cost = data.getInteger();
                    break;
                case 16:
                    members = true;
                    break;
                case 23:
                    modelMale1 = data.getShort();
                    modelMaleTranslateY = data.getUnsignedByte();
                    break;
                case 24:
                    modelMale2 = data.getShort();
                    break;
                case 25:
                    modelFemale1 = data.getShort();
                    modelFemaleTranslateY = data.getUnsignedByte();
                    break;
                case 26:
                    modelFemale2 = data.getShort();
                    break;
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                    groundActions[opcode - 30] = data.getString();
                    break;
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                    interfaceOptions[opcode - 35] = data.getString();
                    break;
                case 40: {
                    int count = data.getUnsignedByte();
                    modelRecolorFind = new short[count];
                    modelRecolorReplace = new short[count];
                    for (int i = 0; i < count; i++) {
                        modelRecolorFind[i] = (short) data.getShort();
                        modelRecolorReplace[i] = (short) data.getShort();
                    }
                    break;
                }
                case 41:
                    int count = data.getUnsignedByte();
                    textureFind = new short[count];
                    textureReplace = new short[count];
                    for (int i = 0; i < count; i++) {
                        textureFind[i] = (short) data.getShort();
                        textureReplace[i] = (short) data.getShort();
                    }
                    break;
                case 42:
                    data.skip(1);
                    break;
                case 65:
                    tradable = true;
                    break;
                case 78:
                    modelMale3 = data.getShort();
                    break;
                case 79:
                    modelFemale3 = data.getShort();
                    break;
                case 90:
                    maleHeadModel1 = data.getShort();
                    break;
                case 91:
                    femaleHeadModel1 = data.getShort();
                    break;
                case 92:
                    maleHeadModel2 = data.getShort();
                    break;
                case 93:
                    femaleHeadModel2 = data.getShort();
                    break;
                case 95:
                    rotateZ = data.getShort();
                    break;
                case 97:
                    noteItemId = data.getShort();
                    break;
                case 98:
                    noteTemplateId = data.getShort();
                    break;
                case 100:
                case 101:
                case 102:
                case 103:
                case 104:
                case 105:
                case 106:
                case 107:
                case 108:
                case 109:
                    if (stackVariantId == null) {
                        stackVariantId = new int[10];
                        stackVariantSize = new int[10];
                    }
                    stackVariantId[opcode - 100] = data.getShort();
                    stackVariantSize[opcode - 100] = data.getShort();
                    break;
                case 110:
                    modelScaleX = data.getShort();
                    break;
                case 111:
                    modelScaleY = data.getShort();
                    break;
                case 112:
                    modelScaleZ = data.getShort();
                    break;
                case 113:
                    lightIntensity = data.getByte();
                    break;
                case 114:
                    lightMagnitude = data.getByte();
                    break;
                case 115:
                    teamNumber = data.getUnsignedByte();
                    break;
                case 139:
                case 140:
                case 148:
                case 149:
                    data.skip(2);
                    break;
                default:
                    throw new CacheException("Unrecognized opcode: " + opcode);
            }
        }
    }

    public JSONObject json() {
        JSONObject result = new JSONObject();
        result.put("inventoryModel", inventoryModel);
        result.put("name", name);
        result.put("rotationLength", rotationLength);
        result.put("rotateX", rotateX);
        result.put("rotateY", rotateY);
        result.put("translateX", translateX);
        result.put("translateY", translateY);
        result.put("stackable", stackable);
        result.put("cost", cost);
        result.put("members", members);
        result.put("modelMale1", modelMale1);
        result.put("modelMaleTranslateY", modelMaleTranslateY);
        result.put("modelMale2", modelMale2);
        result.put("modelFemale1", modelFemale1);
        result.put("modelFemaleTranslateY", modelFemaleTranslateY);
        result.put("modelFemale2", modelFemale2);
        result.put("groundActions", groundActions);
        result.put("interfaceOptions", interfaceOptions);
        result.put("modelRecolorFind", modelRecolorFind);
        result.put("modelRecolorReplace", modelRecolorReplace);
        result.put("textureFind", textureFind);
        result.put("textureReplace", textureReplace);
        result.put("tradable", tradable);
        result.put("modelMale3", modelMale3);
        result.put("modelFemale3", modelFemale3);
        result.put("maleHeadModel1", maleHeadModel1);
        result.put("femaleHeadModel1", femaleHeadModel1);
        result.put("maleHeadModel2", maleHeadModel2);
        result.put("femaleHeadModel2", femaleHeadModel2);
        result.put("rotateZ", rotateZ);
        result.put("noteItemId", noteItemId);
        result.put("noteTemplateId", noteTemplateId);
        result.put("stackVariantId", stackVariantId);
        result.put("stackVariantSize", stackVariantSize);
        result.put("modelScaleX", modelScaleX);
        result.put("modelScaleY", modelScaleY);
        result.put("modelScaleZ", modelScaleZ);
        result.put("lightIntensity", lightIntensity);
        result.put("lightMagnitude", lightMagnitude);
        result.put("teamNumber", teamNumber);
        return result;
    }

    @Override
    public String toString() {
        return json().toString();
    }
}