package ecs;

import ecs.component.Component;
import ecs.component.ComponentManager;
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

    public static void destroyEntity(EntityHandle entityHandle) {
        if(entityExists(entityHandle)) {
            availableEntityIds.add(entityHandle.id());
            ++generation[entityHandle.id()];
            ComponentManager.removeAllComponents(entityHandle.id());
        }
    }

    public static void addComponent(EntityHandle entityHandle, Component component) {
        if(entityExists(entityHandle)) {
           ComponentManager.add(entityHandle.id(), component); 
        }
    }

    // this function might become deprecated because I will want to return the array of comonent
    // and maniplulate that in Query so that only one Hash look up is done per Component Array
    public static <T extends Component> T getComponent(EntityHandle entityHandle, Class<T> componentClass) {
        // check of entity exists
        if(entityExists(entityHandle)) {
            Component component = ComponentManager.get(entityHandle.id(), componentClass);
            return componentClass.cast(component);
        }
        
        // throw error later
        return null;
    }

    private static boolean entityExists(EntityHandle entityHandle) {
        return entityHandle.generation() == generation[entityHandle.id()];
    }






}

