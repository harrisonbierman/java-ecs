package test;

import ecs.EntityHandle;
import ecs.World;
import ecs.component.ColliderComponent;
import ecs.component.Component;
import ecs.component.HealthComponent;
import ecs.component.NameComponent;
import ecs.component.PositionComponent;

public class Test {

    public static void main(String[] args) throws Exception {

        World world = new World();

        EntityHandle entityHandle = world.spawn();
        EntityHandle entityHandle1 = world.spawn();

        for (int i = 0; i < 100; i ++) {
            world.spawn();
        }

        world.addComponent(entityHandle, new NameComponent("Steve"));
        world.addComponent(entityHandle, new HealthComponent(16));
        world.addComponent(entityHandle, new PositionComponent(23, 44));

        world.addComponent(entityHandle1, new NameComponent("Adam"));
        world.addComponent(entityHandle1, new ColliderComponent(2.5f, 4.4f, 1.2f));

        Component steveName = world.getComponent(entityHandle, NameComponent.class);
        Component myHealth = world.getComponent(entityHandle, HealthComponent.class);
        Component myPosition = world.getComponent(entityHandle, PositionComponent.class);

        Component adamName = world.getComponent(entityHandle1, NameComponent.class);
        Component myCollider = world.getComponent(entityHandle1, ColliderComponent.class);

        System.out.println(steveName);
        System.out.println(myHealth);
        System.out.println(myPosition);

        System.out.println(adamName);
        System.out.println(myCollider);

        world.destroy(entityHandle);

        System.out.println(steveName);
        

        System.out.println(HealthComponent.class.getTypeName());
    }
}
