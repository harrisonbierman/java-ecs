package ecs;

import java.util.HashSet;

public class ArchetypeRegistry {
    private IntArraySet archetypeSet = new IntArraySet();

    // returns if new archetype is added
    boolean resolveArchetype(int[] archetype) {

        return archetypeSet.add(archetype);
    }
}
