package ecs;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class IntArrayMap<V> {
    HashMap<Key, V> hashMap = new HashMap<>();

    V put(int[] key, V value) {
        Key internalKey = new Key(key);
        return hashMap.put(internalKey, value);
    }

    V get(int[] key) {
        Key internalKey = new Key(key);
        return hashMap.get(internalKey);
    }

    int size() {
        return hashMap.size();
    }

    Collection<V> values() {
        return hashMap.values();
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
