package ecs;

@FunctionalInterface
public interface Query2Consumer<A extends Component, B extends Component> {

    void access(EntityHandle entityHandle, A componentA, B componentB);
}
