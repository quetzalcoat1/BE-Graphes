package org.insa.graphs.algorithm.utils;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import java.util.ArrayList;
import java.util.Collection;

import org.insa.graphs.algorithm.shortestpath.ShortestPathAlgorithm;
import org.insa.graphs.algorithm.shortestpath.ShortestPathData;
import org.insa.graphs.algorithm.shortestpath.ShortestPathSolution;

import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
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
        if (parameters.origin == parameters.destination) {
            assertEquals(true, this.solution.isFeasible());
            assertEquals(Status.OPTIMAL, this.solution.getStatus());
        }
    }









}