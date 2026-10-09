package test.systems;

import ecs.Component;
import ecs.EcsSystem;
import ecs.Query;
import ecs.World;
import test.components.PositionComponent;
import test.components.VelocityComponent;

public class UpdatePositionSystem implements EcsSystem {
    // function run in scheduler
    public void run(World world) {
        Query query = world.queryBuilder().with(PositionComponent.class, VelocityComponent.class).build();

        query.foreach((entity, components) -> {
            PositionComponent position = (PositionComponent) components[0];
            VelocityComponent velocity = (VelocityComponent) components[1];

            position.x += velocity.x;
            position.y += velocity.y;
        });
    }
}
