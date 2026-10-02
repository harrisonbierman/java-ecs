package ecs;

@FunctionalInterface
public interface Query1Consumer<A extends Component> {

    void access(EntityHandle entityHandle, A componentA);
}
