package ecs;

import java.util.ArrayList;

class EntityManager {

    // i = entityId
    // [0] = generation
    // [1 to end] archetypeIds
    private final int[] generation = new int[EcsConfig.ENTITY_LIMIT];

    // index: is irrelevant
    // value: if value != 0 indicates index in the entityHandles array is available for a new entity
    private final ArrayList<Integer> availableEntityIds = new ArrayList<>();

    public EntityManager() {
        for (int i = 0; i < generation.length; i++) {
            availableEntityIds.add(i);
        }
    }

    public EntityHandle spawn() {

        if (availableEntityIds.isEmpty()) {
            throw new IndexOutOfBoundsException(
                    "Exceeded entity limit of: " + generation.length
            );
        }
        // remove last element for O(1) operation, acts as a stack
        // removed last because it's a faster operation than removing first and shifting down
        int id = availableEntityIds.remove(availableEntityIds.size() - 1);

        return new EntityHandle(id, generation[id]);
    }

    // called in the flush method at end of frame
    public void destroy(EntityHandle handle) {
        availableEntityIds.add(handle.id());
        ++generation[handle.id()];
    }


    // the validation process does not care if the entity is alive or dead
    // just that the entity exists and is able to be manipulated.
    public void validate(EntityHandle handle) {

        throwIfNull(handle);

        if (!exists(handle)) {
            throw new IllegalArgumentException("Entity does not exist");
        }
    }

    public void throwIfNull(EntityHandle handle) {
        if (handle == null) {
            throw new NullPointerException("Entity handle cannot be null");
        }
    }

    public boolean exists(EntityHandle handle) {
        return generation[handle.id()] == handle.generation();
    }

}

