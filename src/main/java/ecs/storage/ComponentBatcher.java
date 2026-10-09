package ecs.storage;

import ecs.Component;

import java.util.HashSet;

public class ComponentBatcher {

    public ComponentBatch pairedInsertionSort(ComponentBatch batch) {

        int[] ids = batch.ids();
        Component[] components = batch.components();

        for (int i = 1; i < ids.length; i++) {
            int id = ids[i];
            Component component = components[i];

            int j = i - 1;

            while (j >= 0 && ids[j] > id) {
                ids[j + 1] = ids[j];
                components[j + 1] = components[j];
                j--;
            }

            ids[j + 1] = id;
            components[j + 1] = component;
        }

        return new ComponentBatch(ids, components);
    }

    public void duplicateException(ComponentBatch batch) {
        HashSet<Integer> seen = new HashSet<>();
        int[] ids = batch.ids();

        for (int i = 0; i < ids.length; i++) {
            if (!seen.add(ids[i])) {
                throw new IllegalArgumentException(
                        "Cannot process duplicate component: "
                                + batch.components()[i].getClass().getSimpleName()
                );
            }
        }
    }
}
