package ecs;

import java.util.Arrays;
import java.util.HashMap;

public class ComponentStorage {
    IntArrayMap<ArchetypeTable> storage = new IntArrayMap<>();

    ComponentStorage() {
        // build default table for entities with no components
        storage.put(new int[0], new ArchetypeTable(new int[0]));
    }

    void insert(ValidEntityHandle handle) {
        ArchetypeTable table = storage.get(new int[0]);

        int destIndex = table.createEmptySlot();

        table.handles[destIndex] = handle;

    }

    void migrate(
            ValidEntityHandle handle,
            ArchetypeMigrationPlan migrationPlan,
            boolean newArchetype,
            ComponentBatch batch
    ) {

        if (newArchetype) {
            storage.put(migrationPlan.to(), new ArchetypeTable(migrationPlan.to()));
        }

        int[] archetypeFrom = migrationPlan.from();
        int[] archetypeTo = migrationPlan.to();

        int[] addedComponentIds = batch.ids();
        Component[] addedComponents = batch.components();

        ArchetypeTable tableFrom = storage.get(migrationPlan.from());
        ArchetypeTable tableTo = storage.get(migrationPlan.to());

        int targetIndex = tableFrom.getIndex(handle);
        int destIndex = tableTo.createEmptySlot();

        // copies handle ref to new location
        tableTo.handles[destIndex] = handle;

        int f = 0; // tracks From component index
        int a = 0; // tracks added component index

        // copies from either the previous location or the
        // added components into the new archetype table
        for (int i = 0; i < archetypeTo.length; i++) {

            if (archetypeFrom.length != 0 && archetypeTo[i] == archetypeFrom[f]) {
                tableTo.components[i][destIndex] = tableFrom.components[f][targetIndex];
                f++;
            } else if (archetypeTo[i] == addedComponentIds[a]) {
                tableTo.components[i][destIndex] = addedComponents[a];
                a++;
            } else {
                throw new Error("Could not match archetype components");
            }
        }

        // after the copy is complete we remove
        tableFrom.remove(handle);
    }

    ValidEntityHandle[] getEntityHandles(int[] archetype) {
        ArchetypeTable table = storage.get(archetype);
        return table.getEntityHandles();
    }

    Component[] getComponents(int[] archetype, int componentId) {
        ArchetypeTable table = storage.get(archetype);
        return table.getComponents(componentId);
    }

    int[][] getAllArchetypes() {
        int[][] result = new int[storage.size()][0];

        int i = 0;
        for (ArchetypeTable table : storage.values()) {
            result[i] = Arrays.copyOf(table.archetype, table.archetype.length);
            i++;
        }

        return result;
    }
}
