package test;

import ecs.EntityHandle;
import ecs.World;
import ecs.Schedule;
import test.components.*;
import test.systems.PrintPositionSystem;
import test.systems.UpdatePositionSystem;

public class Test {

    public static void main(String[] args) throws Exception {

        World world = new World();

        EntityHandle entityHandle1 = world.spawn();

        for (int i = 0; i < 2; i++) {
            float randpx = (float) (Math.random() * 21);
            float randpy = (float) (Math.random() * 21);
            float randvx = (float) (Math.random() * 2);
            float randvy = (float) (Math.random() * 2);
            EntityHandle entity = world.spawn();
            world.addComponents(
                    entity,
                    new PositionComponent(randpx, randpy),
                    new VelocityComponent(randvx, randvy)
            );
        }

        world.addComponents(
                entityHandle1,
                new NameComponent("Steve"),
                new HealthComponent(16),
                new PositionComponent(23f, 44f),
                new VelocityComponent(1f, 1f)
        );


        class frameCounter {
            int count = 0;
        }

        frameCounter frames = new frameCounter();

        Schedule frameSchedule =
                new Schedule.Builder()
                        .add(new UpdatePositionSystem())
                        .add(new PrintPositionSystem())
                        .build();

        for (; frames.count < 10; frames.count++) {
            frameSchedule.run(world);
        }
    }


}
