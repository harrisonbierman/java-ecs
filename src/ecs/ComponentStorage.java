package ecs;

import ecs.component.Component;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ComponentStorage<T extends Component> {
    T[] componentsArray;
    Class<T> componentClass;

    ComponentStorage(Class<T> componentClass) {
        componentsArray = (T[]) Array.newInstance(componentClass, EcsConfig.ENTITY_LIMIT);
        this.componentClass = componentClass;
    }

    void addComponent(int entityId, T component) {
        componentsArray[entityId] = component;
    }

    void removeComponent(int entityId) {
        componentsArray[entityId] = null;
    }

    private void resize(int newSize) {
        componentsArray = Arrays.copyOf(componentsArray, newSize);
    }

    Class<T> getComponentClass() {
        return componentClass;
    }
}
