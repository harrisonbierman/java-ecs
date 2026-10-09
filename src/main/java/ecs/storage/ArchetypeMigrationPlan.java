package ecs.storage;

public record ArchetypeMigrationPlan(
        int[] from,
        int[] to
) {
}
