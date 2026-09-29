package ecs;

import java.util.ArrayList;
import java.util.Arrays;

public class Schedule implements EcsSystem {
    ArrayList<EcsSystem> systems = new  ArrayList<>();


    public Schedule() {}

    // builder pattern
    public Schedule then(EcsSystem system) {
        systems.add(system);
        return this;
    }

    public void create(EcsSystem... systems) {
        this.systems.addAll(Arrays.asList(systems));
    }

    @Override
    public void run(){
        for (EcsSystem system : systems) {
            system.run();
        }
    }
}
