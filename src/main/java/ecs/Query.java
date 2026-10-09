package ecs;

import ecs.query.QueryExecutor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Function;

public class Query {
    private final QueryExecutor executor;
    private final int[][] userDescriptor;
    private final int[][] normalizedDescriptor;

    private Query(QueryExecutor executor, int[][] userDescriptor, int[][] normalizedDescriptor) {
        this.executor = executor;
        this.userDescriptor = userDescriptor;
        this.normalizedDescriptor = normalizedDescriptor;
    }

    public void foreach(QueryConsumer consumer) {
        executor.foreach(normalizedDescriptor, userDescriptor, consumer);
    }


    public static class Builder {

        QueryExecutor executor;
        int[][] userDescriptor = new int[1][0];
        int[][] normalizedDescriptor = new int[1][0];


        Function<Class<? extends Component>[], int[]> resolveExistingComponentIds;

        public Builder(
                QueryExecutor executor,
                Function<Class<? extends Component>[], int[]> resolveExistingComponentIds
        ) {
            this.executor = executor;
            this.resolveExistingComponentIds = resolveExistingComponentIds;
        }

        public Builder with(Class<? extends Component>... componentClasses) {
            int[] componentIds = resolveExistingComponentIds.apply(componentClasses);

            // throw if duplicate
            HashSet<Integer> seen = new HashSet<>();
            for (int i : componentIds) {
                if (!seen.add(i)) {
                    throw new IllegalArgumentException(
                            "Cannot process duplicate component: " + componentClasses[i].getSimpleName()
                    );
                }
            }

            userDescriptor[0] = Arrays.copyOf(componentIds, componentIds.length);

            // normalize
            Arrays.sort(componentIds);
            normalizedDescriptor[0] = Arrays.copyOf(componentIds, componentIds.length);

            return this;
        }

        public Builder without(Class<? extends Component>... components) {

            return this;
        }

        public Query build() {
            Query query = new Query(executor, userDescriptor, normalizedDescriptor);


            return query;
        }
    }

}
