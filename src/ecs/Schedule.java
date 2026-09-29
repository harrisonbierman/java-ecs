package ecs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BooleanSupplier;

public class Schedule implements EcsSystem {
    ArrayList<EcsSystem> systems = new  ArrayList<>();
    BooleanSupplier supplier;

    public Schedule() {
        this.supplier = () -> true;
    }

    public Schedule(BooleanSupplier supplier) {
        this.supplier = supplier;
    }

    public void create(EcsSystem... systems) {
        this.systems.addAll(Arrays.asList(systems));
    }

    @Override
    public void run(){
        if (supplier.getAsBoolean()) {
            for (EcsSystem system : systems) {
                system.run();
            }
        }
    }
}
