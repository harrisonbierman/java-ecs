package ecs;

import ecs.component.Component;

public class World {
    EntityManager entityManager;
    ComponentManager componentManager;


    public World() {
       entityManager = new EntityManager();
       componentManager = new ComponentManager();
    }

    public EntityHandle spawn() {
       return entityManager.spawn();

    }

    public void destroy(EntityHandle entityHandle) {
        if (entityManager.exists(entityHandle)){
            entityManager.destroy(entityHandle);
            componentManager.removeAllComponents(entityHandle.id());
        }
    }

    public <T extends Component> void addComponent(EntityHandle entityHandle, T component) {
        if(entityManager.exists(entityHandle)) {
            componentManager.addComponent(entityHandle.id(), component);
        }
    }

    public <A extends Component, B extends Component> Query2<A,B> query2(Class<A> classA, Class<B> classB) {
        ComponentStorage<A> componentStorageA = componentManager.getComponentStorage(classA);
        ComponentStorage<B> componentStorageB = componentManager.getComponentStorage(classB);
        return new Query2<>(entityManager, componentStorageA, componentStorageB);
    }

    public <A extends Component> Query1<A> query1(Class<A> classA) {
        ComponentStorage<A> componentStorageA = componentManager.getComponentStorage(classA);
        return new Query1<>(entityManager, componentStorageA);
    }

}
