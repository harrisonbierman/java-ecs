package ecs;

@FunctionalInterface
public interface QueryConsumer {

    void accept(EntityHandle entity, Component... components);
}
