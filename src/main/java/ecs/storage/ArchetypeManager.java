package ecs.storage;

import ecs.EcsConfig;
import ecs.EntityHandle;

import java.util.Arrays;

public class ArchetypeManager {

    private int[][] archetypes = new int[EcsConfig.ENTITY_LIMIT][0];

    public ArchetypeMigrationPlan addComponents(EntityHandle handle, int[] componentIds) {

        int entityId = handle.id();

        // duplicates within the two arrays are guaranteed but not between the two arrays.
        int[] newArchetype = mergeSortedRejectDuplicates(archetypes[entityId], componentIds);
        int[] previousArchetype = archetypes[entityId];

        archetypes[entityId] = newArchetype;

        // return to be used enter an archetypeTable in the component storage
        return new ArchetypeMigrationPlan(previousArchetype, newArchetype);
    }

    private void removeAllComponents(EntityHandle entity) {
        archetypes[entity.id()] = new int[1];
    }

    private int[] getArchetype(int id) {
        return Arrays.copyOf(archetypes[id], archetypes[id].length);
    }

    private int[] mergeSortedRejectDuplicates(int[] a, int[] b) {
        int[] result = new int[a.length + b.length];

        int i = 0;
        int j = 0;
        int k = 0;

        while (i < a.length && j < b.length) {
            if (a[i] < b[j]) {
                result[k++] = a[i++];
            } else if (a[i] == b[j]) {
                throw new IllegalArgumentException("ERROR: Cannot add duplicate Component");
            } else {
                result[k++] = b[j++];
            }
        }

        while (i < a.length) {
            result[k++] = a[i++];
        }

        while (j < b.length) {
            result[k++] = b[j++];
        }

        return result;
    }

}
