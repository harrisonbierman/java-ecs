package ecs;


import java.util.Arrays;

// dynamic table
public class ArchetypeTable {
    int capacity = 16;
    int tableLength = 0;

    final int[] archetype;

    ValidEntityHandle[] handles = new ValidEntityHandle[capacity];
    Component[][] components;


    ArchetypeTable(int[] archetype) {
        this.archetype = archetype;
        components = new Component[archetype.length][capacity];
    }


    int createEmptySlot() {

        ++tableLength;

        // Grow
        if (tableLength >= capacity) {
            capacity *= 2;

            // Grow handles array
            handles = Arrays.copyOf(handles, capacity);

            // Grow all component arrays
            for (int i = 0; i < archetype.length; i++) {
                components[i] = Arrays.copyOf(components[i], capacity);
            }
        }

        // Index of empty slot.
        return tableLength - 1;
    }


    // swap removes entity handle
    void remove(ValidEntityHandle handle) {

        int remove = getIndex(handle);

        if (tableLength <= 0) {
            throw new IndexOutOfBoundsException("Cannot remove components because table is emtpy");
        }

        // swap remove the entity handle
        System.arraycopy(handles, tableLength - 1, handles, remove, 1);

        // swap remove all the components
        for (int i = 0; i < archetype.length; i++) {
            System.arraycopy(components[i], tableLength - 1, components[i], remove, 1);
        }

        --tableLength;
    }

    int getIndex(ValidEntityHandle handle) {
        int targetId = handle.id();
        // linear search for shorter arrays
        // If array == BIG, binary search
        for (int i = 0; i < handles.length; i++) {
            if (handles[i].id() == targetId) {
                return i;
            }
        }

        throw new IllegalArgumentException("Entity id: " + targetId + " is not found in archetype table");

        // binary search for larger arrays

    }

    ValidEntityHandle[] getEntityHandles() {
        return Arrays.copyOf(handles, tableLength);
    }

    Component[] getComponents(int componentId) {
        for (int i = 0; i < archetype.length; i++) {
            if (archetype[i] == componentId) {
                return Arrays.copyOf(components[i], tableLength);
            }
        }
        throw new IllegalArgumentException("Could not find components in storage");
    }
}
