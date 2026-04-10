package org.insa.graphs.algorithm;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.insa.graphs.algorithm.AbstractInputData.Mode;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.GraphStatistics;
import org.insa.graphs.model.AccessRestrictions.AccessMode;
import org.insa.graphs.model.AccessRestrictions.AccessRestriction;

public class ArcInspectorFactory {

    private static class NoFilterByLengthArcInspector implements ArcInspector {

        @Override
        public boolean isAllowed(Arc arc) {
            return true;
        }

        @Override
        public double getCost(Arc arc) {
            return arc.getLength();
        }

        @Override
        public int getMaximumSpeed() {
            return GraphStatistics.NO_MAXIMUM_SPEED;
        }

        @Override
        public Mode getMode() {
            return Mode.LENGTH;
        }

        @Override
        public String toString() {
            return "Shortest path, all roads allowed";
        }
    };

    private static class OnlyCarsByLengthArcInspector
            extends NoFilterByLengthArcInspector {

        @Override
        public boolean isAllowed(Arc arc) {
            return arc.getRoadInformation().getAccessRestrictions().isAllowedForAny(
                    AccessMode.MOTORCAR,
                    EnumSet.complementOf(EnumSet.of(AccessRestriction.FORBIDDEN,
                            AccessRestriction.PRIVATE)));
        }

        @Override
        public String toString() {
            return "Shortest path, only roads open for cars";
        }
    };

    private static class OnlyCarsByTimeArcInspector
            extends NoFilterByLengthArcInspector {

        @Override
        public double getCost(Arc arc) {
            return arc.getMinimumTravelTime();
        }

        @Override
        public Mode getMode() {
            return Mode.TIME;
        }

        @Override
        public String toString() {
            return "Fastest path, all roads allowed";
        }
    };

    private static class OnlyPedestrianByTime implements ArcInspector {

        static final int maxPedestrianSpeed = 5;

        @Override
        public boolean isAllowed(Arc arc) {
            return arc.getRoadInformation().getAccessRestrictions().isAllowedForAny(
                    AccessMode.FOOT,
                    EnumSet.complementOf(EnumSet.of(AccessRestriction.FORBIDDEN,
                            AccessRestriction.PRIVATE)));
        }

        @Override
        public double getCost(Arc arc) {
            return arc.getTravelTime(Math.min(maxPedestrianSpeed,
                    arc.getRoadInformation().getMaximumSpeed()));
        }

        @Override
        public String toString() {
            return "Fastest path for pedestrian";
        }

        @Override
        public int getMaximumSpeed() {
            return 5;
        }

        @Override
        public Mode getMode() {
            return Mode.TIME;
        }
    };

    /**
     * @return List of all arc filters in this factory.
     */
    public static List<ArcInspector> getAllFilters() {
        // Add your own filters here (do not forget to implement toString()
        // to get an understandable output!):
        return Arrays.asList(new NoFilterByLengthArcInspector(),
                new OnlyCarsByLengthArcInspector(), new OnlyCarsByTimeArcInspector(),
                new OnlyPedestrianByTime());
    }

}
