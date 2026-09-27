package ecs.component;

import ecs.EcsConfig;
import java.util.HashMap;
import java.util.Map;
// holds lookup tables for all entity components
// needs to sync with the enity manager
public class ComponentManager {

    private static ColliderComponent[] colliderComponentsArray = new ColliderComponent[EcsConfig.ENTITY_LIMIT];
    private static DamageComponent[] damageComponentsArray = new DamageComponent[EcsConfig.ENTITY_LIMIT];
    private static HealthComponent[] healthComponentsArray = new HealthComponent[EcsConfig.ENTITY_LIMIT];
    private static NameComponent[] nameComponentsArray = new NameComponent[EcsConfig.ENTITY_LIMIT];
    private static PositionComponent[] positionComponentsArray = new PositionComponent[EcsConfig.ENTITY_LIMIT];
    private static VelocityComponent[] velocityComponentsArray = new VelocityComponent[EcsConfig.ENTITY_LIMIT];

    private static Map<Class<? extends Component>, Component[]> componentsArrayMap = new HashMap<>();


    // should not have an instance
    private ComponentManager(){};

    // initialization
    static {
        componentsArrayMap.put(ColliderComponent.class, colliderComponentsArray);
        componentsArrayMap.put(DamageComponent.class, damageComponentsArray);
        componentsArrayMap.put(HealthComponent.class, healthComponentsArray);
        componentsArrayMap.put(NameComponent.class, nameComponentsArray);
        componentsArrayMap.put(PositionComponent.class, positionComponentsArray);
        componentsArrayMap.put(VelocityComponent.class, velocityComponentsArray);
    }

    public static void add(int entityId, Component component) {
        componentsArrayMap.get(component.getClass())[entityId] = component;
    }

    public static Component get(int entityId, Class<? extends Component> componentClass) {
        return componentsArrayMap.get(componentClass)[entityId];
    }

    public static void removeAllComponents(int entityId) {
        for (var componentsArrayEntry : componentsArrayMap.entrySet()) {
            Component[] componentsArray = componentsArrayEntry.getValue();
            componentsArray[entityId] = null;
        }
        
    }
}
