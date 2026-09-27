package ecs.component;

import ecs.EcsConfig;
// holds lookup tables for all entity components
// needs to sync with the enity manager
public class ComponentManager {

    private static ColliderComponent[] colliderComponentsArray = new ColliderComponent[EcsConfig.ENTITY_LIMIT];
    private static DamageComponent[] damageComponentsArray = new DamageComponent[EcsConfig.ENTITY_LIMIT];
    private static HealthComponent[] healthComponentsArray = new HealthComponent[EcsConfig.ENTITY_LIMIT];
    private static NameComponent[] nameComponentsArray = new NameComponent[EcsConfig.ENTITY_LIMIT];
    private static PositionComponent[] positionComponentsArray = new PositionComponent[EcsConfig.ENTITY_LIMIT];
    private static VelocityComponent[] velocityComponentsArray = new VelocityComponent[EcsConfig.ENTITY_LIMIT];

    private static Component[][] allComponentArrays = {
        colliderComponentsArray,
        damageComponentsArray,
        healthComponentsArray,
        nameComponentsArray,
        positionComponentsArray,
        velocityComponentsArray
    };

    // should not have an instance
    private ComponentManager(){};

    // initialization
    static {

    }

    public static void add(int entityId, Component component) {
        String componentName = component.getClass().getSimpleName();

        switch(componentName) {
           case "ColliderComponent" -> colliderComponentsArray[entityId] = (ColliderComponent)component;
           case "DamageComponent" -> damageComponentsArray[entityId] = (DamageComponent)component;
           case "HealthComponent" -> healthComponentsArray[entityId] = (HealthComponent)component;
           case "NameComponent" -> nameComponentsArray[entityId] =   (NameComponent)component;
           case "PositionComponent" -> positionComponentsArray[entityId] = (PositionComponent)component;
           case "VelocityComponent" -> velocityComponentsArray[entityId] = (VelocityComponent)component;
        }
    }

    public static Component get(int entityId, ComponentType componentType) {
        Component component = switch(componentType) {
            case COLLIDER -> colliderComponentsArray[entityId];
            case DAMAGE -> damageComponentsArray[entityId];
            case HEALTH -> healthComponentsArray[entityId];
            case NAME -> nameComponentsArray[entityId];
            case POSITION ->  positionComponentsArray[entityId];
            case VELOCITY ->  velocityComponentsArray[entityId];
            // throw error later
            default ->  null;
        };
        return component;
    }

    public static void removeAllComponents(int entityId) {
        for (Component[] componentArray : allComponentArrays) {
            componentArray[entityId] = null;
        }
        
    }
}
