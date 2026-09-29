package ecs;

import ecs.component.Component;

@FunctionalInterface
public interface Query2Consumer<A extends Component, B extends Component>{

    void access(EntityHandle entityHandle, A componentA, B componentB);
}
