package io.runecache.contents.map;

final class HeightUtilities {

    private static final int[] COSINE_TABLE;

    static {
        int length = 2048;
        double radians = Math.toRadians(360D / length);
        COSINE_TABLE = new int[length];
        for (int i = 0; i < length; i++) {
            COSINE_TABLE[i] = (int) (0x10000 * Math.cos(i * radians));
        }
    }

    static int calculateTileHeight(int x, int y, int baseX, int baseY) {
        x += baseX + 932731;
        y += baseY + 556238;
        int noise = interpolateNoise(x + 45365, y + 91923, 4) - 128 + (interpolateNoise(x + 10294, y + 37821, 2) - 128 >> 1) + (interpolateNoise(x, y, 1) - 128 >> 2);
        noise = (int) (0.3 * noise) + 35;
        noise = Math.min(Math.max(noise, 10), 60);
        return -noise * 8;
    }

    private static int interpolateNoise(int x, int y, int scale) {
        int scaledX = x / scale;
        int scaledY = y / scale;
        int valueX = x & scale - 1;
        int valueY = y & scale - 1;
        int a = generateSmoothedNoise(scaledX, scaledY);
        int b = generateSmoothedNoise(scaledX + 1, scaledY);
        int c = generateSmoothedNoise(scaledX, scaledY + 1);
        int d = generateSmoothedNoise(scaledX + 1, scaledY + 1);
        a = cosineInterpolation(a, b, valueX, scale);
        b = cosineInterpolation(c, d, valueX, scale);
        return cosineInterpolation(a, b, valueY, scale);
    }

    private static int generateSmoothedNoise(int x, int y) {
        return (generateSimpleNoise(x - 1, y - 1) + generateSimpleNoise(x + 1, y - 1) + generateSimpleNoise(x - 1, y + 1) + generateSimpleNoise(x + 1, y + 1)) / 16
                + (generateSimpleNoise(x - 1, y) + generateSimpleNoise(x + 1, y) + generateSimpleNoise(x, y - 1) + generateSimpleNoise(x, y + 1)) / 8
                + generateSimpleNoise(x, y) / 4;
    }

    private static int generateSimpleNoise(int x, int y) {
        int noise = x + y * 57;
        noise ^= noise << 13;
        return ((noise * (noise * noise * 15731 + 789221) + 1376312589) & Integer.MAX_VALUE) >> 19 & 0xFF;
    }

    private static int cosineInterpolation(int start, int end, int value, int valueScale) {
        value = 0x10000 - COSINE_TABLE[(value * 1024) / valueScale] >> 1;
        return (start * (0x10000 - value) >> 16) + (end * value >> 16);
    }

    private HeightUtilities() {

    }
}