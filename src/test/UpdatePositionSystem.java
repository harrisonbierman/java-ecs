package test;

import ecs.World;
import ecs.Query2;
import ecs.component.PositionComponent;
import ecs.component.VelocityComponent;

public class UpdatePositionSystem {
    Query2<PositionComponent, VelocityComponent> queryPositionVelocity;

    // initialize query for game lifetime
    public UpdatePositionSystem(World world) {
        queryPositionVelocity = world.query2(PositionComponent.class, VelocityComponent.class);

    }

    // function run in scheduler
    public void run() {
       queryPositionVelocity.
               forEach(((entityHandle, position, velocity) -> {
          position.x += velocity.x;
          velocity.y += velocity.y;
       }));
    }
}
