package ecs;

import java.util.Arrays;
import java.util.HashSet;

public class IntArraySet {
    HashSet<Key> hashSet = new HashSet<>();

    boolean add(int[] key) {
        Key internalKey = new Key(key);
        return hashSet.add(internalKey);
    }

    private static final class Key {

        int[] value;


        Key(int[] value) {
            // very important to copy because we don't want the
            // reference, we want the actual values
            this.value = Arrays.copyOf(value, value.length);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Key other &&
                    Arrays.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(value);
        }
    }
}
