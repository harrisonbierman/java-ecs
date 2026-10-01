package ecs;

import test.components.*;

import java.util.HashMap;

class ComponentManager {

    // database for entities and their components
    private HashMap<Class<? extends Component>, ComponentStorage<? extends Component>> componentStorageHashMap = new HashMap<>();

    ComponentManager(){
        register(ColliderComponent.class);
        register(DamageComponent.class);
        register(HealthComponent.class);
        register(NameComponent.class);
        register(PositionComponent.class);
        register(VelocityComponent.class);
    }

    private <T extends Component> void register(Class<T> componentClass) {
        ComponentStorage<T> componentStorage = new ComponentStorage<>(componentClass);
        componentStorageHashMap.put(componentClass, componentStorage);
    }

    <T extends Component> void addComponent(int entityId, T component) {
         ComponentStorage<T> componentStorage = (ComponentStorage<T>) componentStorageHashMap.get(component.getClass());
         componentStorage.addComponent(entityId, component);
    }

    <T extends Component> ComponentStorage<T> getComponentStorage(Class<T> componentClass) {
        return (ComponentStorage<T>) componentStorageHashMap.get(componentClass);
    }

    void removeAllComponents(int entityId) {
        for (var componentStorage : componentStorageHashMap.values()){
            componentStorage.removeComponent(entityId);
        }
        
    }
}
