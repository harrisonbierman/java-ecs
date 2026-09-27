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

   public void addComponent(EntityHandle entityHandle, Component component) {
    if(entityManager.exists(entityHandle)) {
        componentManager.addComponent(entityHandle.id(), component);
    }
   }

   // This should not be in the final API this is just for
   // testing purposes, Query is the only public API that
   // should be able to get comonents in any capacity
   public <T extends Component> T getComponent(EntityHandle entityHandle, Class<T> componentClass) {
    if(entityManager.exists(entityHandle)){
        return componentManager.getComponent(entityHandle.id(), componentClass);
    }
    return null;
   }

   // also should not be in public API
   public <T extends Component> T[] getComponentArray(Class<T> componentClass) {
        return componentManager.getComponentArray(componentClass);
   }

}
