package org.insa.graphs.algorithm.utils;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.insa.graphs.algorithm.shortestpath.ShortestPathAlgorithm;
import org.insa.graphs.algorithm.shortestpath.ShortestPathData;
import org.insa.graphs.algorithm.shortestpath.ShortestPathSolution;

import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.algorithm.ArcInspector;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public abstract class ShortestPathAlgorithmTest {


    public abstract ShortestPathAlgorithm createAlgorithm(ShortestPathData data);


    protected static class TestParameters {

        // Data to use
        public final Graph graph;
        public final Node origin;
        public final Node destination;
        public final ArcInspector arcInspector;

        // data contains values
        // deleteOrder contains indexes in data[]
        public TestParameters(Graph graph, Node origin, Node destination, ArcInspector arcInspector) {
            this.graph = graph;
            this.origin = origin;
            this.destination = destination;
            this.arcInspector = arcInspector;
        }
    };

    /**
     * Set of parameters.
     */
    @Parameters
    public static Collection<Object> data() {
        Collection<Object> objects = new ArrayList<>();

        // Empty graph
        objects.add(new TestParameters(new Graph("", "", new ArrayList<Node>(), null), null, null, null));

        return objects;
    }

    @Parameter
    public TestParameters parameters;
    
    // Actual algorithm.
    private ShortestPathAlgorithm algorithm;

    // The solution to the run.
    private ShortestPathSolution solution;

    @Before
    public void init() {

        ShortestPathData data = new ShortestPathData(
            parameters.graph,
            parameters.origin,
            parameters.destination,
            parameters.arcInspector
        );

        // Create the algorithm
        this.algorithm = createAlgorithm(data);

        solution = this.algorithm.run();
    }

    @Test
    public void testIsEmpty() {
        assertEquals(parameters.graph.size() == 0, !this.solution.isFeasible());
    }




}