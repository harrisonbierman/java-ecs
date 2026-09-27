package app;
import ecs.EntityHandle;
import ecs.EntityManager;
import ecs.component.ColliderComponent;
import ecs.component.Component;
import ecs.component.ComponentType;
import ecs.component.HealthComponent;
import ecs.component.NameComponent;
import ecs.component.PositionComponent;

public class App {

    public static void main(String[] args) throws Exception {

        EntityHandle entityHandle = EntityManager.createEntity();
        EntityHandle entityHandle1 = EntityManager.createEntity();

        EntityManager.addComponent(entityHandle, new NameComponent("Steve"));
        EntityManager.addComponent(entityHandle, new HealthComponent(16));
        EntityManager.addComponent(entityHandle, new PositionComponent(23, 44));

        EntityManager.addComponent(entityHandle1, new NameComponent("Adam"));
        EntityManager.addComponent(entityHandle1, new ColliderComponent(2.5f, 4.4f, 1.2f));

        Component steveName = EntityManager.getComponent(entityHandle, ComponentType.NAME);
        Component myHealth = EntityManager.getComponent(entityHandle, ComponentType.HEALTH);
        Component myPosition = EntityManager.getComponent(entityHandle, ComponentType.POSITION);

        Component adamName = EntityManager.getComponent(entityHandle1, ComponentType.NAME);
        Component myCollider = EntityManager.getComponent(entityHandle1, ComponentType.COLLIDER);

        System.out.println(steveName);
        System.out.println(myHealth);
        System.out.println(myPosition);

        System.out.println(adamName);
        System.out.println(myCollider);

        EntityManager.destroyEntity(entityHandle);

        System.out.println(steveName);
    }
}
