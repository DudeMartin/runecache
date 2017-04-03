package io.runecache.contents;

import io.runecache.*;

import java.io.IOException;
import java.util.Arrays;

public class ItemDefinition {

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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[ inventoryModel: ");
        builder.append(inventoryModel);
        builder.append("\n\tname: ");
        builder.append(name);
        builder.append("\n\trotationLength: ");
        builder.append(rotationLength);
        builder.append("\n\trotateX: ");
        builder.append(rotateX);
        builder.append("\n\trotateY: ");
        builder.append(rotateY);
        builder.append("\n\ttranslateX: ");
        builder.append(translateX);
        builder.append("\n\ttranslateY: ");
        builder.append(translateY);
        builder.append("\n\tstackable: ");
        builder.append(stackable);
        builder.append("\n\tcost: ");
        builder.append(cost);
        builder.append("\n\tmembers: ");
        builder.append(members);
        builder.append("\n\tmodelMale1: ");
        builder.append(modelMale1);
        builder.append("\n\tmodelMaleTranslateY: ");
        builder.append(modelMaleTranslateY);
        builder.append("\n\tmodelMale2: ");
        builder.append(modelMale2);
        builder.append("\n\tmodelFemale1: ");
        builder.append(modelFemale1);
        builder.append("\n\tmodelFemaleTranslateY: ");
        builder.append(modelFemaleTranslateY);
        builder.append("\n\tmodelFemale2: ");
        builder.append(modelFemale2);
        builder.append("\n\tgroundActions: ");
        builder.append(Arrays.toString(groundActions));
        builder.append("\n\tinterfaceOptions: ");
        builder.append(Arrays.toString(interfaceOptions));
        builder.append("\n\tmodelRecolorFind: ");
        builder.append(Arrays.toString(modelRecolorFind));
        builder.append("\n\tmodelRecolorReplace: ");
        builder.append(Arrays.toString(modelRecolorReplace));
        builder.append("\n\ttextureFind: ");
        builder.append(Arrays.toString(textureFind));
        builder.append("\n\ttextureReplace: ");
        builder.append(Arrays.toString(textureReplace));
        builder.append("\n\ttradable: ");
        builder.append(tradable);
        builder.append("\n\tmodelMale3: ");
        builder.append(modelMale3);
        builder.append("\n\tmodelFemale3: ");
        builder.append(modelFemale3);
        builder.append("\n\tmaleHeadModel1: ");
        builder.append(maleHeadModel1);
        builder.append("\n\tfemaleHeadModel1: ");
        builder.append(femaleHeadModel1);
        builder.append("\n\tmaleHeadModel2: ");
        builder.append(maleHeadModel2);
        builder.append("\n\tfemaleHeadModel2: ");
        builder.append(femaleHeadModel2);
        builder.append("\n\trotateZ: ");
        builder.append(rotateZ);
        builder.append("\n\tnoteItemId: ");
        builder.append(noteItemId);
        builder.append("\n\tnoteTemplateId: ");
        builder.append(noteTemplateId);
        builder.append("\n\tstackVariantId: ");
        builder.append(java.util.Arrays.toString(stackVariantId));
        builder.append("\n\tstackVariantSize: ");
        builder.append(java.util.Arrays.toString(stackVariantSize));
        builder.append("\n\tmodelScaleX: ");
        builder.append(modelScaleX);
        builder.append("\n\tmodelScaleY: ");
        builder.append(modelScaleY);
        builder.append("\n\tmodelScaleZ: ");
        builder.append(modelScaleZ);
        builder.append("\n\tlightIntensity: ");
        builder.append(lightIntensity);
        builder.append("\n\tlightMagnitude: ");
        builder.append(lightMagnitude);
        builder.append("\n\tteamNumber: ");
        builder.append(teamNumber);
        builder.append(" ]");
        return builder.toString();
    }
}