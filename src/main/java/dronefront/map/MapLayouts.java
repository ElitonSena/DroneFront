package dronefront.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapLayouts {
    private static final List<int[][]> layouts = new ArrayList<>();

    static {
        layouts.add(new int[][] {
            {0, 2}, {4, 2}, {4, 11}, {9, 11}, {9, 2}, {14, 2}, {14, 11}, {19, 11}, {19, 2}, {24, 2}, {24, 6}, {27, 6}
        });

        layouts.add(new int[][] {
            {0, 12}, {5, 12}, {5, 2}, {20, 2}, {20, 12}, {25, 12}, {25, 6}, {27, 6}
        });

        layouts.add(new int[][] {
            {0, 0}, {26, 0}, {26, 12}, {2, 12}, {2, 3}, {22, 3}, {22, 9}, {6, 9}, {6, 6}, {27, 6}
        });
        
        layouts.add(new int[][] {
            {0, 6}, {12, 6}, {12, 2}, {16, 2}, {16, 11}, {12, 11}, {12, 8}, {20, 8}, {20, 5}, {27, 5}
        });

        layouts.add(new int[][] {
            {0, 1}, {25, 1}, {25, 12}, {5, 12}, {5, 6}, {27, 6}
        });

        layouts.add(new int[][] {
            {0, 12}, {4, 12}, {4, 10}, {8, 10}, {8, 8}, {12, 8}, {12, 6}, {16, 6}, {16, 4}, {20, 4}, {20, 2}, {24, 2}, {24, 0}, {27, 0}
        });

        layouts.add(new int[][] {
            {0, 7}, {5, 7}, {5, 2}, {10, 2}, {10, 11}, {15, 11}, {15, 2}, {20, 2}, {20, 7}, {27, 7}
        });

        layouts.add(new int[][] {
            {0, 7}, {5, 7}, {5, 6}, {10, 6}, {10, 8}, {15, 8}, {15, 6}, {20, 6}, {20, 7}, {27, 7}
        });

        layouts.add(new int[][] {
            {0, 0}, {2, 0}, {2, 12}, {25, 12}, {25, 2}, {27, 2}
        });

        layouts.add(new int[][] {
            {0, 1}, {3, 1}, {3, 12}, {6, 12}, {6, 1}, {9, 1}, {9, 12}, {12, 12}, {12, 1}, {15, 1}, {15, 12}, {18, 12}, {18, 1}, {21, 1}, {21, 12}, {24, 12}, {24, 6}, {27, 6}
        });
    }

    public static int[][] getRandomLayout() {
        Random rand = new Random();
        return layouts.get(rand.nextInt(layouts.size()));
    }
}