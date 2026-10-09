package ecs.storage;


import ecs.Component;
import ecs.EntityHandle;

import java.util.Arrays;

// dynamic table
class ArchetypeTable {
    int capacity = 16;
    int tableLength = 0;

    public final int[] archetype;

    public EntityHandle[] handles = new EntityHandle[capacity];
    public Component[][] components;


    ArchetypeTable(int[] archetype) {
        this.archetype = archetype;
        components = new Component[archetype.length][capacity];
    }


    public int createEmptySlot() {

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
    public void remove(EntityHandle handle) {

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

    public int getIndex(EntityHandle handle) {
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

    public EntityHandle[] getEntityHandles() {
        return Arrays.copyOf(handles, tableLength);
    }

    public Component[] getComponents(int componentId) {
        for (int i = 0; i < archetype.length; i++) {
            if (archetype[i] == componentId) {
                return Arrays.copyOf(components[i], tableLength);
            }
        }
        throw new IllegalArgumentException("Could not find components in storage");
    }
}
