package test;

import ecs.EcsSystem;
import ecs.World;
import ecs.Query2;
import ecs.component.PositionComponent;
import ecs.component.VelocityComponent;

public class UpdatePositionSystem implements EcsSystem {
    // function run in scheduler
    public void run(World world) {
        Query2<PositionComponent, VelocityComponent> query =
                world.query2(PositionComponent.class, VelocityComponent.class);

        query. forEach(((entityHandle, position, velocity) -> {
                position.x += velocity.x;
                position.y += velocity.y;
        }));
    }
}
