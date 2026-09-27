package ecs;

public final class EntityHandle {
    final int id;
    final int generation;

    // no public keyword means its package-private
    EntityHandle(int id, int generation) {
        this.id = id;
        this.generation = generation;
    }

    public int id() {
        return id;
    }

    public int generation() {
        return generation;
    }
    
}