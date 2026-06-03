package org.insa.graphs.algorithm.utils;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import java.util.ArrayList;
import java.util.Collection;

import org.insa.graphs.algorithm.shortestpath.BellmanFordAlgorithm;
import org.insa.graphs.algorithm.shortestpath.ShortestPathAlgorithm;
import org.insa.graphs.algorithm.shortestpath.ShortestPathData;
import org.insa.graphs.algorithm.shortestpath.ShortestPathSolution;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Point;
import org.insa.graphs.model.io.BinaryGraphReader;
import org.insa.graphs.model.io.GraphReader;
import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.algorithm.ArcInspectorFactory;
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
        public final Status expectedStatus;

        // data contains values
        // deleteOrder contains indexes in data[]
        public TestParameters(Graph graph, Node origin, Node destination, ArcInspector arcInspector, Status expectedStatus) {
            this.graph = graph;
            this.origin = origin;
            this.destination = destination;
            this.arcInspector = arcInspector;
            this.expectedStatus = expectedStatus;
        }
    };

    public static Graph getGraph(String mapName) throws Exception {
        try (final GraphReader reader = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))))) {
            return reader.read();
        }
    }

    /**
     * Set of parameters.
     */
    @Parameters
    public static Collection<Object> data() throws Exception {
        Collection<Object> objects = new ArrayList<>();

        Graph emptyGraph = new Graph("", "", new ArrayList<Node>(), null);
        Graph bretagneGraph = getGraph("/C:/Users/natha/Desktop/INSA/3A/BE Graphes/maps/bretagne.mapgr");

        // Empty graph
        objects.add(new TestParameters(emptyGraph, null, null, null, Status.INFEASIBLE));

        // Origin and destination not connected
        objects.add(new TestParameters(bretagneGraph, bretagneGraph.get(642480), bretagneGraph.get(619897), ArcInspectorFactory.getAllFilters().get(0), Status.INFEASIBLE));

        // Origin and destination are the same
        objects.add(new TestParameters(bretagneGraph, bretagneGraph.get(642480), bretagneGraph.get(642480), ArcInspectorFactory.getAllFilters().get(0), Status.OPTIMAL));

        // One or both nodes do not exist
        Node nonExistingNode = new Node(-1, new Point(0, 0));
        objects.add(new TestParameters(bretagneGraph, nonExistingNode, bretagneGraph.get(642480), ArcInspectorFactory.getAllFilters().get(0), Status.INFEASIBLE));
        objects.add(new TestParameters(bretagneGraph, bretagneGraph.get(642480), nonExistingNode, ArcInspectorFactory.getAllFilters().get(0), Status.INFEASIBLE));
        objects.add(new TestParameters(bretagneGraph, nonExistingNode, nonExistingNode, ArcInspectorFactory.getAllFilters().get(0), Status.INFEASIBLE));



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
        if (parameters.graph.size() == 0) {
            assertEquals(false, this.solution.isFeasible());
            assertEquals(Status.INFEASIBLE, this.solution.getStatus());
        }
    }

    @Test
    public void testOriginEqualsDestination() {
        // Check that if origin and destination are the same and exist in the graph, the solution is feasible and optimal
        if (parameters.origin == parameters.destination && parameters.origin != null && parameters.destination != null && !(parameters.origin.getId() >= parameters.graph.size() || parameters.destination.getId() >= parameters.graph.size() || parameters.origin.getId() < 0 || parameters.destination.getId() < 0)) {
            assertEquals(true, this.solution.isFeasible());
        }
    }

    @Test
    public void testExpectedStatus() {
        assertEquals(parameters.expectedStatus, this.solution.getStatus());
    }

    @Test
    public void testComparisonWithBellmanFord() {
        
        // Bellman-Ford does not behave the same way as Dijkstra or A* when origin == destination.
        if (!this.solution.isFeasible() || parameters.origin == parameters.destination) {
            return;
        }

        BellmanFordAlgorithm bellmanFord = new BellmanFordAlgorithm(this.solution.getInputData());
        ShortestPathSolution bellmanFordSolution = bellmanFord.run();

        //compute cost :
        double algorithmCost = 0;
        double bellmanFordCost = 0;
    
        for (Arc arc : bellmanFordSolution.getPath().getArcs()) {
            bellmanFordCost += bellmanFordSolution.getInputData().getCost(arc);
        }
        
        for (Arc arc : this.solution.getPath().getArcs()) {
            algorithmCost += this.solution.getInputData().getCost(arc);
        }

        assertEquals(bellmanFordCost, algorithmCost);
    }








}