package ecs;

public final class Query2<A extends Component, B extends Component> {

    private final ComponentStorage<A> componentStorageA;
    private final ComponentStorage<B> componentStorageB;
    private final EntityManager entityManager;

    public Query2(EntityManager entityManager, ComponentStorage<A> componentStorageA, ComponentStorage<B> componentStorageB) {
        this.entityManager = entityManager;
        this.componentStorageA = componentStorageA;
        this.componentStorageB = componentStorageB;
    }

    public void forEach(Query2Consumer<A, B> query2Consumer) {
        A[] componentArrayA = componentStorageA.getArray();
        B[] componentArrayB = componentStorageB.getArray();

        for (int id = 0; id < componentArrayA.length; id++) {
            A componentA = componentArrayA[id];
            B componentB = componentArrayB[id];

            if (componentA != null && componentB != null) {
                query2Consumer.access(entityManager.getHandle(id), componentA, componentB);
            }
        }
    }
}
