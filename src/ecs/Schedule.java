package ecs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BooleanSupplier;

public class Schedule implements EcsSystem {
    ArrayList<EcsSystem> systems = new  ArrayList<>();
    BooleanSupplier supplier;

    public Schedule(BooleanSupplier supplier, ArrayList<EcsSystem> systems) {
        this.systems = systems;
        this.supplier = supplier;
    }

    public static class Builder {
        ArrayList<EcsSystem> systems = new ArrayList<>();
        BooleanSupplier supplier;

        public Builder(){
            this.supplier = () -> true;
        }

        public Builder(BooleanSupplier supplier){
            this.supplier = supplier;
        }

        public Builder add(EcsSystem system) {
            this.systems.add(system);
            return this;
        }

        public Schedule build() {
            return new Schedule(this.supplier, this.systems);
        }
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
