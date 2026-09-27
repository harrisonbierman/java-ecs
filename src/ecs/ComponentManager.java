package ecs;

import ecs.component.*;
import java.util.HashMap;

class ComponentManager {

    // database for entites and their comonents
    private HashMap<Class<? extends Component>, Component[]> componentsArrayMap = new HashMap<>();

    ComponentManager(){
        componentsArrayMap.put(ColliderComponent.class, new ColliderComponent[EcsConfig.ENTITY_LIMIT]);
        componentsArrayMap.put(DamageComponent.class, new DamageComponent[EcsConfig.ENTITY_LIMIT]);
        componentsArrayMap.put(HealthComponent.class, new HealthComponent[EcsConfig.ENTITY_LIMIT]);
        componentsArrayMap.put(NameComponent.class, new NameComponent[EcsConfig.ENTITY_LIMIT]);
        componentsArrayMap.put(PositionComponent.class, new PositionComponent[EcsConfig.ENTITY_LIMIT]);
        componentsArrayMap.put(VelocityComponent.class, new VelocityComponent[EcsConfig.ENTITY_LIMIT]);
    }

    void addComponent(int entityId, Component component) {
        componentsArrayMap.get(component.getClass())[entityId] = component;
    }

    // the T makes sure a specific class is passed back instead of Component, it could be HealthComponent
    <T extends Component> T getComponent(int entityId, Class<T> componentClass) {
        Component[] componentsArray = componentsArrayMap.get(componentClass);

        if(componentsArray == null) {
            throw new IllegalArgumentException(
                componentClass.getSimpleName() + " is not registered with ComponentManager"
            );
        }

        return componentClass.cast(componentsArray[entityId]);
    }

    void removeAllComponents(int entityId) {
        for (var componentsArrayEntry : componentsArrayMap.entrySet()) {
            Component[] componentsArray = componentsArrayEntry.getValue();
            componentsArray[entityId] = null;
        }
        
    }
}
