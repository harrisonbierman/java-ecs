package test.systems;

import ecs.EcsSystem;
import ecs.World;
import ecs.Query2;
import test.components.NameComponent;
import test.components.PositionComponent;

public class PrintPositionSystem implements EcsSystem {

    public void run(World world) {
        Query2<
                PositionComponent,
                NameComponent
                >
                query = world.query2(
                PositionComponent.class,
                NameComponent.class
        );

        query.forEach((entityHandle, position, name) -> {
            System.out.println("Entity ID: " + entityHandle.id());
            System.out.println("    Name: " + name.name);
            System.out.println("    Position x: " + position.x + " y: " + position.y);
            System.out.println();
        });
    }
}
