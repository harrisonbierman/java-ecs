package ecs;

import java.util.ArrayList;

class EntityManager {


    // index: is the entity ID the 
    // value: is secondary ID or generation
    private int[] generation = new int[EcsConfig.ENTITY_LIMIT];
    
    // index: is irrelavent
    // value: if value != 0 indicates index in the entityHandles array is available for a new entity
    private ArrayList<Integer> availableEntityIds = new ArrayList<>();

    EntityManager(){
        for(int i = 0; i < generation.length; i++) {
           availableEntityIds.add(i);
       } 
    }

    EntityHandle spawn() {
        
        // remove last element for O(1) operation, acts as a stack
        try {
            int id = availableEntityIds.remove(availableEntityIds.size() - 1);
            // destroyEntity() increases the generation by 1 for validation
            // that is why we do not increase it on creation
            return new EntityHandle(id, generation[id]);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Exceeded Entity Limit of " + EcsConfig.ENTITY_LIMIT + ". increase EscConfig.ENTITY_LIMIT");
        }

        return null;
    }

    void destroy(EntityHandle entityHandle) {
            availableEntityIds.add(entityHandle.id());
            ++generation[entityHandle.id()];
    }

    boolean exists(EntityHandle entityHandle) {
        return entityHandle.generation() == generation[entityHandle.id()];
    }

    





}

