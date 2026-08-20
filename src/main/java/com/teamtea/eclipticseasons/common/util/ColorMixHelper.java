package com.teamtea.eclipticseasons.common.util;

public class ColorMixHelper {
    /**
     * Performs bilinear interpolation between four packed 32-bit colors.
     *
     * <pre>
     * m00 -------- m10
     *  |             |
     *  |   (x, y)    |
     *  |             |
     * m01 -------- m11
     * </pre>
     *
     * The exact channel order does not matter, as each byte is interpolated
     * independently and remains in its original position.
     */
    public static int mix2d(
            int m00,
            int m01,
            int m10,
            int m11,
            float x,
            float y
    ) {
        int x1 = normalizedFloatToByte(x);
        int y1 = normalizedFloatToByte(y);

        int x0 = 255 - x1;
        int y0 = 255 - y1;

        int channel0 = mixChannel(m00, m01, m10, m11, 0,  x0, x1, y0, y1);
        int channel1 = mixChannel(m00, m01, m10, m11, 8,  x0, x1, y0, y1);
        int channel2 = mixChannel(m00, m01, m10, m11, 16, x0, x1, y0, y1);
        int channel3 = mixChannel(m00, m01, m10, m11, 24, x0, x1, y0, y1);

        return channel0
                | channel1 << 8
                | channel2 << 16
                | channel3 << 24;
    }

    private static int mixChannel(
            int m00,
            int m01,
            int m10,
            int m11,
            int shift,
            int x0,
            int x1,
            int y0,
            int y1
    ) {
        int c00 = m00 >>> shift & 0xFF;
        int c01 = m01 >>> shift & 0xFF;
        int c10 = m10 >>> shift & 0xFF;
        int c11 = m11 >>> shift & 0xFF;

        int row0 = mixByte(c00, c10, x0, x1);
        int row1 = mixByte(c01, c11, x0, x1);

        return mixByte(row0, row1, y0, y1);
    }

    private static int mixByte(int a, int b, int weightA, int weightB) {
        return (a * weightA + b * weightB + 127) / 255;
    }

    private static int normalizedFloatToByte(float value) {
        if (value <= 0.0F) {
            return 0;
        }

        if (value >= 1.0F) {
            return 255;
        }

        return Math.round(value * 255.0F);
    }
}
