package ecs;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;

public class Schedule implements EcsSystem {
    private final EcsSystem[] systems;
    private final BooleanSupplier supplier;

    private Schedule(BooleanSupplier supplier, EcsSystem[] systems) {
        this.systems = systems;
        this.supplier = supplier;
    }

    public static class Builder {
        private final ArrayList<EcsSystem> systems = new ArrayList<>();
        private final BooleanSupplier supplier;

        public Builder() {
            this.supplier = () -> true;
        }

        public Builder(BooleanSupplier supplier) {
            this.supplier = supplier;
        }

        public Builder add(EcsSystem system) {
            this.systems.add(system);
            return this;
        }

        public Schedule build() {
            return new Schedule(
                    this.supplier,
                    this.systems.toArray(new EcsSystem[0])
            );
        }
    }

    @Override
    public void run(World world) {
        if (supplier.getAsBoolean()) {
            for (EcsSystem system : systems) {
                system.run(world);
            }
        }
    }
}
