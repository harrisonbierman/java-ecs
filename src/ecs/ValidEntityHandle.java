package ecs;

import javax.swing.text.html.parser.Entity;

// consider getting rid of the genration all together
// it is not important information after it's been validated
final class ValidEntityHandle {
    private final int id;
    private final int generation;

    // no public keyword means its package-private
    ValidEntityHandle(int id, int generation) {
        this.id = id;
        this.generation = generation;
    }

    public int id() {
        return id;
    }

    public EntityHandle invalidate() {
        return new EntityHandle(this.id, this.generation);
    }
}
