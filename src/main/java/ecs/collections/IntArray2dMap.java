package ecs.collections;

import java.util.Arrays;
import java.util.HashMap;

public class IntArray2dMap<V> {
    HashMap<Key, V> hashMap = new HashMap<>();

    public V put(int[][] key, V value) {
        Key internalKey = new Key(key);
        return hashMap.put(internalKey, value);
    }

    public V get(int[][] key) {
        Key internalKey = new Key(key);
        return hashMap.get(internalKey);
    }

    private static final class Key {

        int[][] value;


        Key(int[][] value) {
            this.value = new int[value.length][];
            for (int i = 0; i < value.length; i++) {
                this.value[i] = Arrays.copyOf(value[i], value[i].length);
            }
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Key other &&
                    // seems like I might be able to use Array.deepEquals
                    Arrays.deepEquals(value, other.value);
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(value);
        }


        // didn't even need this because there is already Arrays.deepEquals
        private boolean compare2dArray(int[][] a, int[][] b) {
            if (a.length != b.length) {
                return false;
            }

            for (int i = 0; i < a.length; i++) {
                if (!Arrays.equals(a[i], b[i])) {
                    return false;
                }
            }

            return true;
        }
    }
}
