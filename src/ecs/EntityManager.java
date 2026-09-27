package ecs;

import ecs.component.Component;
import ecs.component.ComponentManager;
import ecs.component.ComponentType;
import java.util.ArrayList;

public class EntityManager {


    // index: is the entity ID the 
    // value: is secondary ID or generation
    private static int[] generation = new int[EcsConfig.ENTITY_LIMIT];

    // index: is irrelavent
    // value: if value != 0 indicates index in the entityHandles array is available for a new entity
    private static ArrayList<Integer> availableEntityIds = new ArrayList<>();

    // should never be an instance, utility/single class
    private EntityManager(){}

    // initialization
    static {
        for(int i = 0; i < generation.length; i++) {
           availableEntityIds.add(i);
       } 
    }

    public static EntityHandle createEntity() {
        
        int id = availableEntityIds.remove(0);
        int nextGeneration = ++generation[id];

        EntityHandle entityHandle =  new EntityHandle(id, nextGeneration);
        generation[id] = nextGeneration;
        return entityHandle;
    }

    public static void destroyEntity(EntityHandle entityHandle) {
        if(entityExists(entityHandle)) {
            availableEntityIds.add(entityHandle.id());
            ComponentManager.removeAllComponents(entityHandle.id());
        }
    }

    public static void addComponent(EntityHandle entityHandle, Component component) {
        if(entityExists(entityHandle)) {
           ComponentManager.add(entityHandle.id(), component); 
        }
    }

    public static Component getComponent(EntityHandle entityHandle, ComponentType componentType) {

        // check of entity exists
        if(entityExists(entityHandle)) {
            return ComponentManager.get(entityHandle.id(), componentType);
        }
        
        // throw error later
        return null;
    }

    private static boolean entityExists(EntityHandle entityHandle) {
        return entityHandle.generation() == generation[entityHandle.id()];
    }






}

