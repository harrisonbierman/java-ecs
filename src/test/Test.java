package test;

import ecs.EntityHandle;
import ecs.World;
import ecs.component.*;
import ecs.Schedule;

public class Test {

    public static void main(String[] args) throws Exception {

        World world = new World();

        EntityHandle entityHandle1 = world.spawn();
        EntityHandle entityHandle2 = world.spawn();
        EntityHandle entityHandle3 = world.spawn();

        world.addComponent(entityHandle1, new NameComponent("Steve"));
        world.addComponent(entityHandle1, new HealthComponent(16));
        world.addComponent(entityHandle1, new PositionComponent(23f, 44f));
        world.addComponent(entityHandle1, new VelocityComponent(2.3f, 3.2f));

        world.addComponent(entityHandle2, new NameComponent("Adam"));
        world.addComponent(entityHandle2, new PositionComponent(22f, 33f));
        world.addComponent(entityHandle2, new VelocityComponent(4.5f, -2.4f));
        world.addComponent(entityHandle2, new ColliderComponent(2.5f, 4.4f, 1.2f));

        world.addComponent(entityHandle3, new NameComponent("Chair"));
        world.addComponent(entityHandle3, new PositionComponent(22f, 33f));
        world.addComponent(entityHandle3, new ColliderComponent(2.5f, 4.4f, 1.2f));

        Schedule schedule = new Schedule();

        // this is one api possiblitly
        schedule.then(new UpdatePositionSystem(world))
                .then(new PrintPositionSystem(world))
                .then(new UpdatePositionSystem(world))
                .then(new PrintPositionSystem(world));

       Schedule schedule2 = new Schedule();

       // I think I like this one better
       schedule2.create(
               new UpdatePositionSystem(world),
               new PrintPositionSystem(world),
               new UpdatePositionSystem(world),
               new PrintPositionSystem(world)
       );

        schedule.run();


    }


}
