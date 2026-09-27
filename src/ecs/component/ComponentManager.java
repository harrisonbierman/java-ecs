package ecs.component;

import ecs.EcsConfig;
import java.util.HashMap;
import java.util.Map;
// holds lookup tables for all entity components
// needs to sync with the enity manager
public class ComponentManager {

    // data base for entites and their comonents
    private static Map<Class<? extends Component>, Component[]> componentsArrayMap = new HashMap<>();

    // should not have an instance
    private ComponentManager(){};

    // initialization
    static {
        componentsArrayMap.put(ColliderComponent.class, new ColliderComponent[EcsConfig.ENTITY_LIMIT]);
        componentsArrayMap.put(DamageComponent.class, new DamageComponent[EcsConfig.ENTITY_LIMIT]);
        componentsArrayMap.put(HealthComponent.class, new HealthComponent[EcsConfig.ENTITY_LIMIT]);
        componentsArrayMap.put(NameComponent.class, new NameComponent[EcsConfig.ENTITY_LIMIT]);
        componentsArrayMap.put(PositionComponent.class, new PositionComponent[EcsConfig.ENTITY_LIMIT]);
        componentsArrayMap.put(VelocityComponent.class, new VelocityComponent[EcsConfig.ENTITY_LIMIT]);
    }

    public static void add(int entityId, Component component) {
        componentsArrayMap.get(component.getClass())[entityId] = component;
    }

    // the T makes sure a specific class is passed back instead of Component, it could be HealthComponent
    public static <T extends Component> T get(int entityId, Class<T> componentClass) {
        Component[] componentsArray = componentsArrayMap.get(componentClass);

        if(componentsArray == null) {
            throw new IllegalArgumentException(
                componentClass.getSimpleName() + " is not registered with ComponentManager"
            );
        }

        Component component = componentsArray[entityId];

        if (component == null) {
            return null;
        }

        return componentClass.cast(component);
    }

    public static void removeAllComponents(int entityId) {
        for (var componentsArrayEntry : componentsArrayMap.entrySet()) {
            Component[] componentsArray = componentsArrayEntry.getValue();
            componentsArray[entityId] = null;
        }
        
    }
}
