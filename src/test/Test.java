package test;
import ecs.EntityHandle;
import ecs.EntityManager;
import ecs.component.ColliderComponent;
import ecs.component.Component;
import ecs.component.HealthComponent;
import ecs.component.NameComponent;
import ecs.component.PositionComponent;

public class Test {

    public static void main(String[] args) throws Exception {

        EntityHandle entityHandle = EntityManager.createEntity();
        EntityHandle entityHandle1 = EntityManager.createEntity();

        EntityManager.addComponent(entityHandle, new NameComponent("Steve"));
        EntityManager.addComponent(entityHandle, new HealthComponent(16));
        EntityManager.addComponent(entityHandle, new PositionComponent(23, 44));

        EntityManager.addComponent(entityHandle1, new NameComponent("Adam"));
        EntityManager.addComponent(entityHandle1, new ColliderComponent(2.5f, 4.4f, 1.2f));

        Component steveName = EntityManager.getComponent(entityHandle, NameComponent.class);
        Component myHealth = EntityManager.getComponent(entityHandle, HealthComponent.class);
        Component myPosition = EntityManager.getComponent(entityHandle, PositionComponent.class);

        Component adamName = EntityManager.getComponent(entityHandle1, NameComponent.class);
        Component myCollider = EntityManager.getComponent(entityHandle1, ColliderComponent.class);

        System.out.println(steveName);
        System.out.println(myHealth);
        System.out.println(myPosition);

        System.out.println(adamName);
        System.out.println(myCollider);

        EntityManager.destroyEntity(entityHandle);

        System.out.println(steveName);
        

        System.out.println(HealthComponent.class.getTypeName());
    }
}
