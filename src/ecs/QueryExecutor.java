package ecs;

import javax.swing.text.html.parser.Entity;
import java.util.Arrays;

public class QueryExecutor {
    QueryCache queryCache;
    ComponentStorage componentStorage;

    QueryExecutor(QueryCache queryCache, ComponentStorage componentStorage) {
        this.queryCache = queryCache;
        this.componentStorage = componentStorage;
    }

    void foreach(int[][] normalizedQueryDescriptor, int[][] userQueryDescriptor, QueryConsumer queryConsumer) {
        int[][] matches = queryCache.get(normalizedQueryDescriptor);

        // update if descriptor is not cached 
        // does not yet update when storage actually changes
        if (matches == null) {
            matches = findMatches(normalizedQueryDescriptor);
            queryCache.update(normalizedQueryDescriptor, matches);
        }

        // filter out components user wants
        int[] targetUserComponents = userQueryDescriptor[0];


        ValidEntityHandle[] finalHandles = new ValidEntityHandle[0];
        Component[][] finalComponents = new Component[targetUserComponents.length][0];
        int totalLength = 0;
        int tail = 0;

        // in the future that I know will never happen
        // I'm going to implement some kind of step matching algorithm
        // this one is literally checking every element with every element
        for (int i = 0; i < matches.length; i++) {
            ValidEntityHandle[] partialHandles = componentStorage.getEntityHandles(matches[i]);
            totalLength += partialHandles.length;

            finalHandles = Arrays.copyOf(finalHandles, totalLength);

            // append
            System.arraycopy(partialHandles, 0, finalHandles, tail, partialHandles.length);


            for (int j = 0; j < matches[i].length; j++) { // each matching archetype
                for (int k = 0; k < targetUserComponents.length; k++) { // each component user asked for
                    Component[] partialComponents = componentStorage.getComponents(matches[i], targetUserComponents[k]);

                    finalComponents[k] = Arrays.copyOf(finalComponents[k], totalLength);

                    System.arraycopy(partialComponents, 0, finalComponents[k], tail, partialComponents.length);
                }
            }

            tail = totalLength;
        }

        // Don't give the user back internal handles
        EntityHandle[] externalHandles = new EntityHandle[totalLength];
        for (int i = 0; i < finalHandles.length; i++) {
            externalHandles[i] = finalHandles[i].invalidate();
        }

        for (int i = 0; i < totalLength; i++) {
            Component[] entityComponents = new Component[targetUserComponents.length];
            for (int j = 0; j < targetUserComponents.length; j++) {
                entityComponents[j] = finalComponents[j][i];
            }
            queryConsumer.accept(externalHandles[i], entityComponents);
        }


    }

    int[][] findMatches(int[][] queryDescriptor) {
        int[][] allArchetypes = componentStorage.getAllArchetypes();

        // allocated for maximum matches
        int[][] matches = new int[allArchetypes.length][0];

        int archetypeMatchCount = 0;

        // there has to be a better way than a triple for loop
        for (int i = 0; i < allArchetypes.length; i++) { // each archetype

            int componentMatchCount = 0;

            for (int j = 0; j < allArchetypes[i].length; j++) { // each componentId in archetype


                for (int k = 0; k < queryDescriptor[0].length; k++) { // each component in "with" section of descriptor

                    if (allArchetypes[i][j] == queryDescriptor[0][k]) {
                        componentMatchCount++;
                    }

                    if (componentMatchCount == queryDescriptor[0].length) {
                        matches[archetypeMatchCount] = Arrays.copyOf(allArchetypes[i], allArchetypes[i].length);
                        archetypeMatchCount++;

                        // this is a feel bad solution. I needed to reset the componentMatchCount
                        // after it has been reached because it is copying duplicate archetypes into
                        // because it still loops even after all component matches have been found
                        componentMatchCount = 0;
                    }
                }
            }
        }

        // shrink down array to size of actual number of matches
        return Arrays.copyOf(matches, archetypeMatchCount);
    }
}
