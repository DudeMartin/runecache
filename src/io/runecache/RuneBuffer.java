package io.runecache;

public class RuneBuffer {

    public final byte[] bytes;
    public int offset;

    public RuneBuffer(byte[] bytes) {
        this.bytes = bytes;
    }

    public RuneBuffer(int capacity) {
        this(new byte[capacity]);
    }

    public void skip(int amount) {
        offset += amount;
    }

    public void putInteger(int value) {
        bytes[offset++] = (byte) (value >> 24);
        bytes[offset++] = (byte) (value >> 16);
        bytes[offset++] = (byte) (value >> 8);
        bytes[offset++] = (byte) value;
    }

    public int getByte() {
        return bytes[offset++];
    }

    public int getUnsignedByte() {
        return bytes[offset++] & 0xFF;
    }

    public int getShort() {
        return ((bytes[offset++] & 0xFF) << 8) | (bytes[offset++] & 0xFF);
    }

    public int getTriplet() {
        return ((bytes[offset++] & 0xFF) << 16) | ((bytes[offset++] & 0xFF) << 8) | (bytes[offset++] & 0xFF);
    }

    public int getInteger() {
        return ((bytes[offset++] & 0xFF) << 24)
                | ((bytes[offset++] & 0xFF) << 16)
                | ((bytes[offset++] & 0xFF) << 8)
                | (bytes[offset++] & 0xFF);
    }

    public int getSmartInteger() {
        return (bytes[offset] < 0) ? getInteger() : getShort();
    }

    public int getUnsignedSmartShort() {
        return ((bytes[offset] & 0xff) < 128) ? getUnsignedByte() : getShort() - 32768;
    }

    public void getBytes(byte[] destination) {
        System.arraycopy(bytes, offset, destination, 0, destination.length);
        offset += destination.length;
    }

    public void xteaDecipher(int[] key, int start, int end) {
        int delta = 0x9E3779B9;
        int mark = offset;
        offset = start;
        for (int i = 0, pairs = (end - start) / 8; i < pairs; i++) {
            int first = getInteger();
            int second = getInteger();
            int rounds = 32;
            int sum = delta * rounds;
            while (rounds-- > 0) {
                second -= (((first << 4) ^ (first >>> 5)) + first) ^ (sum + key[(sum >>> 11) & 3]);
                sum -= delta;
                first -= (((second << 4) ^ (second >>> 5)) + second) ^ (sum + key[sum & 3]);
            }
            offset -= 8;
            putInteger(first);
            putInteger(second);
        }
        offset = mark;
    }
}