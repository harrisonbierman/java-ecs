package ecs;

public class World {
    EntityManager entityManager;

    ArchetypeManager archetypeManager;
    ArchetypeRegistry archetypeRegistry;

    ComponentTypeRegistry componentTypeRegistry;
    ComponentStorage componentStorage;
    ComponentBatcher componentBatcher;

    QueryCache queryCache;
    QueryExecutor queryExecutor;


    public World() {
        entityManager = new EntityManager();

        archetypeManager = new ArchetypeManager();
        archetypeRegistry = new ArchetypeRegistry();

        componentTypeRegistry = new ComponentTypeRegistry();
        componentStorage = new ComponentStorage();
        componentBatcher = new ComponentBatcher();

        queryCache = new QueryCache();
        queryExecutor = new QueryExecutor(queryCache, componentStorage);
    }

    public EntityHandle spawn() {
        EntityHandle handle = entityManager.spawn();

        ValidEntityHandle valid = entityManager.validate(handle);

        // I'm not sure if everything breaks if I don't
        // add it to an empty storage first, but I'm not
        // here to find out right now. Test later.
        componentStorage.insert(valid);

        return handle;
    }


    // public facing API simply calls it destroy, really we are queueing it
    // to be destroyed at the end of the frame.
    public void destroy(EntityHandle handle) {

        ValidEntityHandle valid = entityManager.validate(handle);

        entityManager.destroy(valid);

    }

    public boolean exists(EntityHandle handle) {
        entityManager.throwIfNull(handle);

        return entityManager.exists(handle);
    }

    public <T extends Component> void addComponents(EntityHandle handle, Component... components) {

        ValidEntityHandle valid = entityManager.validate(handle);

        Class<? extends Component>[] componentClasses = componentTypeRegistry.resolveComponentClasses(components);
        int[] componentIds = componentTypeRegistry.resolveComponentIds(componentClasses);

        ComponentBatch batch = new ComponentBatch(componentIds, components);
        componentBatcher.duplicateException(batch);
        batch = componentBatcher.pairedInsertionSort(batch);

        ArchetypeMigrationPlan migrationPlan = archetypeManager.addComponents(valid, batch.ids());
        boolean newArchetype = archetypeRegistry.resolveArchetype(migrationPlan.to());

        componentStorage.migrate(valid, migrationPlan, newArchetype, batch);

    }

    public Query.Builder queryBuilder() {
        return new Query.Builder(queryExecutor, componentTypeRegistry::resolveExistingComponentIds);
    }

}
