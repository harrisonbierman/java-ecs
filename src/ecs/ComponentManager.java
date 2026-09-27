package ecs;

import ecs.component.*;
import java.lang.reflect.Array;
import java.util.HashMap;

class ComponentManager {

    // database for entites and their comonents
    private HashMap<Class<? extends Component>, Component[]> componentsArrayMap = new HashMap<>();

    ComponentManager(){
        register(ColliderComponent.class);
        register(DamageComponent.class);
        register(HealthComponent.class);
        register(NameComponent.class);
        register(PositionComponent.class);
        register(VelocityComponent.class);
    }

    private <T extends Component> void register(Class<T> componentClass) {
        T[] componentArray = (T[])Array.newInstance(componentClass, EcsConfig.ENTITY_LIMIT);
        componentsArrayMap.put(componentClass, componentArray);
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

    <T extends Component> T[] getComponentArray(Class<T> componentClass) {
        return (T[])componentsArrayMap.get(componentClass);
    }

    void removeAllComponents(int entityId) {
        for (var componentsArrayEntry : componentsArrayMap.entrySet()) {
            Component[] componentsArray = componentsArrayEntry.getValue();
            componentsArray[entityId] = null;
        }
        
    }
}
