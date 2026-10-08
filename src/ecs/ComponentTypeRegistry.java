package ecs;

import java.util.HashMap;
import java.util.HashSet;

class ComponentTypeRegistry {

    // Maps that go in opposite directions
    private HashMap<Class<? extends Component>, Integer> componentMap = new HashMap<>();
    private HashMap<Integer, Class<? extends Component>> classMap = new HashMap<>();

    private int nextId = 0;


    int[] resolveComponentIds(Class<? extends Component>[] componentClasses) {
        int[] result = new int[componentClasses.length];

        for (int i = 0; i < componentClasses.length; i++) {
            Integer id = componentMap.get(componentClasses[i]);

            if (id == null) {
                // bi-directional maps
                componentMap.put(componentClasses[i], nextId);
                classMap.put(nextId, componentClasses[i]);
                id = nextId++;
            }

            result[i] = id;
        }

        return result;
    }


    int[] resolveExistingComponentIds(Class<? extends Component>[] componentClasses) {

        int[] result = new int[componentClasses.length];

        for (int i = 0; i < componentClasses.length; i++) {
            Class<? extends Component> componentClass = componentClasses[i];
            Integer id = componentMap.get(componentClass);

            if (id == null) {
                throw new IllegalArgumentException(
                        "Component class is not registered: " + componentClass.getSimpleName()
                                + ". Suggestion: Component classes are registered automatically when"
                                + " adding components to entities."
                );
            }

            result[i] = id;
        }

        return result;
    }

    // we can guarantee that there is reversibility
    @SuppressWarnings("unchecked")
    Class<? extends Component>[] resolveClasses(int[] archetype) {
        Class<? extends Component>[] componentClasses =
                (Class<? extends Component>[]) new Class<?>[archetype.length];

        for (int i = 0; i < archetype.length; i++) {
            componentClasses[i] = classMap.get(archetype[i]);
        }

        return componentClasses;
    }

    Class<? extends Component>[] resolveComponentClasses(Component[] components) {
        Class<? extends Component>[] result = new Class[components.length];

        for (int i = 0; i < components.length; i++) {
            result[i] = components[i].getClass();
        }

        return result;
    }

}
