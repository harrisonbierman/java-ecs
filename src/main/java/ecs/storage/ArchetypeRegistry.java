package ecs.storage;

import ecs.collections.IntArraySet;

public class ArchetypeRegistry {
    private IntArraySet archetypeSet = new IntArraySet();

    // returns if new archetype is added
    public boolean resolveArchetype(int[] archetype) {

        return archetypeSet.add(archetype);
    }
}
