package test;

import ecs.EcsSystem;
import ecs.World;
import ecs.Query2;
import ecs.component.NameComponent;
import ecs.component.PositionComponent;

public class PrintPositionSystem implements EcsSystem {
    Query2<PositionComponent, NameComponent> query;

    PrintPositionSystem(World world) {
        query= world.query2(PositionComponent.class, NameComponent.class);
    }

    public void run() {
        query.forEach((entityHandle, position, name) -> {
            System.out.println("Entity ID: " + entityHandle.id());
            System.out.println("    Name: " + name.name);
            System.out.println("    Position x: " + position.x + " y: " + position.y);
            System.out.println();
        });
    }
}
