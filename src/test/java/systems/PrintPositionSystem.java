package test.systems;

import ecs.EcsSystem;
import ecs.Query;
import ecs.World;
import test.components.NameComponent;
import test.components.PositionComponent;
import test.components.VelocityComponent;

public class PrintPositionSystem implements EcsSystem {

    public void run(World world) {
        Query query = world.queryBuilder().with(NameComponent.class, PositionComponent.class, VelocityComponent.class).build();
        Query query2 = world.queryBuilder().with(PositionComponent.class, VelocityComponent.class).build();

        query.foreach((entity, components) -> {
            NameComponent name = (NameComponent) components[0];
            PositionComponent position = (PositionComponent) components[1];
            VelocityComponent velocity = (VelocityComponent) components[2];

            System.out.println("Name: " + name.name);
            System.out.println("Position: x:" + position.x + " y:" + position.y);
        });

        query2.foreach((entity, components) -> {
            PositionComponent position = (PositionComponent) components[0];
            VelocityComponent velocity = (VelocityComponent) components[1];

            System.out.println("Position: x:" + position.x + " y:" + position.y);
        });
    }
}
