package ecs;

public final class Query1<A extends Component> {

    private final ComponentStorage<A> componentStorageA;
    private final EntityManager entityManager;

    public Query1(EntityManager entityManager, ComponentStorage<A> componentStorageA) {
        this.entityManager = entityManager;
        this.componentStorageA = componentStorageA;
    }

    public void forEach(Query1Consumer<A> query1Consumer){
        A[] componentArrayA = componentStorageA.getArray();

        for(int id = 0; id < componentArrayA.length; id++){
            A componentA = componentArrayA[id];

            if(componentA != null){
                query1Consumer.access(entityManager.getHandle(id), componentA);
            }
        }
    }
}

