package ecs;

// this is not a record specifically because I require that the
// constructor is package-private, record does not allow that distinction.
public final class EntityHandle {
    private final int id;
    private final int generation;

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