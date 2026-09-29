package test;

import ecs.EcsSystem;
import ecs.World;
import ecs.Query2;
import ecs.component.PositionComponent;
import ecs.component.VelocityComponent;

public class UpdatePositionSystem implements EcsSystem {
    Query2<PositionComponent, VelocityComponent> query;

    // initialize query for game lifetime
    public UpdatePositionSystem(World world) {
        query = world.query2(PositionComponent.class, VelocityComponent.class);

    }

    // function run in scheduler
    public void run() {
       query.
               forEach(((entityHandle, position, velocity) -> {
          position.x += velocity.x;
          position.y += velocity.y;
       }));
    }
}
