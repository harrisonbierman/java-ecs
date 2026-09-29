package ecs;

import java.util.ArrayList;
import java.util.Arrays;

public class Schedule implements EcsSystem {
    ArrayList<EcsSystem> systems = new  ArrayList<>();

    public Schedule() {}

    public void create(EcsSystem... systems) {
        this.systems.addAll(Arrays.asList(systems));
    }

    @Override
    public void run(){
        for (EcsSystem system : systems) {
            system.run();
        }
    }

    public <T> void runIf(boolean condition) {
       if(condition) {
           for (EcsSystem system : systems) {
               system.run();
           }
       }
    }
}
