package com.teamtea.eclipticseasons.client.model.bakequad;

public class DirectionMask {
    public final static int[][][] DIRECTIONS = new int[][][]{
            // 下启
            {{0, 1}},
            {{0, 1}, {1, 0}},
            {{0, 1}, {-1, 0}},
            {{0, 1}, {1, 0}, {-1, 0}},
            {{0, 1}, {0, -1}, {-1, 0}},
            {{0, 1}, {0, -1}, {1, 0}, {-1, 0}},

            // 左启
            {{-1, 0}},
            {{-1, 0}, {0, -1}},
            {{-1, 0}, {0, -1}, {1, 0}},

            // 上启
            {{0, -1}},
            {{0, -1}, {1, 0}},

            // 右启动
            {{1, 0}},
            {{1, 0}, {0, -1}, {0, 1}},

            {{0, 1}, {0, -1}},
            {{1, 0}, {-1, 0}},
    };

    public final static int[] INDEXS = new int[]{
            1, 3, 4, 5, 6, 8,
            9, 11, 13,
            15, 10,
            7, 12,
            17, 18
    };
}
