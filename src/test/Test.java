package test;

import ecs.EntityHandle;
import ecs.World;
import ecs.Query2;
import ecs.component.*;

import java.util.Arrays;

public class Test {

    public static void main(String[] args) throws Exception {

        World world = new World();

        EntityHandle entityHandle1 = world.spawn();
        EntityHandle entityHandle2 = world.spawn();

        world.addComponent(entityHandle1, new NameComponent("Steve"));
        world.addComponent(entityHandle1, new HealthComponent(16));
        world.addComponent(entityHandle1, new PositionComponent(23f, 44f));
        world.addComponent(entityHandle1, new VelocityComponent(2.3f, 3.2f));

        world.addComponent(entityHandle2, new NameComponent("Adam"));
        world.addComponent(entityHandle2, new PositionComponent(22f, 33f));
        world.addComponent(entityHandle2, new VelocityComponent(4.5f, -2.4f));
        world.addComponent(entityHandle2, new ColliderComponent(2.5f, 4.4f, 1.2f));

        Query2<PositionComponent, VelocityComponent> queryPositionVelocity =
                world.query2(PositionComponent.class, VelocityComponent.class);

        queryPositionVelocity
                .forEach((entityHandle, position, velocity) -> {
                    System.out.println(entityHandle.id());
                    System.out.println(position);
                    System.out.println(velocity);
                    position.x = velocity.x;
                    position.y = velocity.y;
                    System.out.println(entityHandle.id());
                    System.out.println(position);
                    System.out.println(velocity);

                });
    }
}
