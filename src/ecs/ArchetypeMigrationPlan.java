package ecs;

public record ArchetypeMigrationPlan(
        int[] from,
        int[] to
) {
}
