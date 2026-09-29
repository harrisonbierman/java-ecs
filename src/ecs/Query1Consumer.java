package ecs;

import ecs.component.Component;

@FunctionalInterface
public interface Query1Consumer<A extends Component>{

    void access(EntityHandle entityHandle, A componentA);
}
