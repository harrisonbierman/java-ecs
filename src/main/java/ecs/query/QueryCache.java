package ecs.query;

import ecs.collections.IntArray2dMap;

public class QueryCache {
    IntArray2dMap<int[][]> cacheMap = new IntArray2dMap<>();


    // returns null of no matches are found
    int[][] get(int[][] queryDescriptor) {
        return cacheMap.get(queryDescriptor);
    }

    void update(int[][] queryDescriptor, int[][] matches) {
        cacheMap.put(queryDescriptor, matches);
    }

}
