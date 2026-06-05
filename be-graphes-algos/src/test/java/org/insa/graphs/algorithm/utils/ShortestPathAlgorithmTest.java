package org.insa.graphs.algorithm.utils;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.insa.graphs.algorithm.shortestpath.BellmanFordAlgorithm;
import org.insa.graphs.algorithm.shortestpath.ShortestPathAlgorithm;
import org.insa.graphs.algorithm.shortestpath.ShortestPathData;
import org.insa.graphs.algorithm.shortestpath.ShortestPathSolution;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.GraphStatistics;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.Point;
import org.insa.graphs.model.io.BinaryGraphReader;
import org.insa.graphs.model.io.GraphReader;
import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.algorithm.ArcInspectorFactory;
import org.insa.graphs.algorithm.AbstractInputData.Mode;
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

        GraphStatistics graphStatistics = new GraphStatistics(null, 0, 0,10, 0.0f);

        Graph emptyGraph = new Graph("", "", new ArrayList<Node>(), graphStatistics);

        // Version INSA :
        //Graph bretagneGraph = getGraph("/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/bretagne.mapgr");
        //Graph toulouseGraph = getGraph("/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/toulouse.mapgr");

        // Version personnelle :
        Graph bretagneGraph = getGraph("/C:/Users/natha/Desktop/INSA/3A/BE Graphes/maps/bretagne.mapgr");
        Graph toulouseGraph = getGraph("/C:/Users/natha/Desktop/INSA/3A/BE Graphes/maps/toulouse.mapgr");

        Node nonExistingNode = new Node(-1, new Point(0, 0));

        // Test cases for all filters implemented in ArcInspectorFactory
        for (ArcInspector arcInspector : ArcInspectorFactory.getAllFilters()) {
            // Empty graph
            objects.add(new TestParameters(emptyGraph, null, null, null, Status.INFEASIBLE));

            // Origin and destination not connected
            objects.add(new TestParameters(bretagneGraph, bretagneGraph.get(642480), bretagneGraph.get(619897), arcInspector, Status.INFEASIBLE));

            // Origin and destination are the same
            objects.add(new TestParameters(bretagneGraph, bretagneGraph.get(642480), bretagneGraph.get(642480), arcInspector, Status.OPTIMAL));

            // One or both nodes do not exist
            objects.add(new TestParameters(bretagneGraph, nonExistingNode, bretagneGraph.get(642480), arcInspector, Status.INFEASIBLE));
            objects.add(new TestParameters(bretagneGraph, bretagneGraph.get(642480), nonExistingNode, arcInspector, Status.INFEASIBLE));
            objects.add(new TestParameters(bretagneGraph, nonExistingNode, nonExistingNode, arcInspector, Status.INFEASIBLE));

            // Short rural path
            objects.add(new TestParameters(bretagneGraph, bretagneGraph.get(326847), bretagneGraph.get(355023), arcInspector, Status.OPTIMAL));

            // Short urban path
            objects.add(new TestParameters(bretagneGraph, bretagneGraph.get(1866), bretagneGraph.get(32098), arcInspector, Status.OPTIMAL));

            // Long path (>300 km)
            objects.add(new TestParameters(bretagneGraph, bretagneGraph.get(639854), bretagneGraph.get(347688), arcInspector, Status.OPTIMAL));

            // Curved path
            objects.add(new TestParameters(toulouseGraph, toulouseGraph.get(21210), toulouseGraph.get(1012), arcInspector, Status.OPTIMAL));
        }

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
    public void testIsValid() {
        if (this.solution.isFeasible()) {
            assertEquals(true, this.solution.getPath().isValid());
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

        // Comparison with Bellman-Ford is too long for huge graph
        if (parameters.graph.size() > 1000) {
            return;
        } 
        
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

    @Test
    public void testPathIsOptimal() {

        if (!this.solution.isFeasible() || parameters.origin == parameters.destination) {
            return;
        }

        // This verification does not work with TIME search
        // because Path.createFastestPathFromNodes does not take our speed (via arcInspector) into account when choosing fastest arc,
        // it takes into account the road maximum speed (via RoadInformation).
        if (parameters.arcInspector.getMode() == Mode.TIME) {
            return;
        }

        double algorithmCost = 0;

        // Calculate algorithmCost and Transform the solution path into a list of nodes
        List<Node> nodes = new ArrayList<>();
        Arc lastArc = null;
        for (Arc arc : this.solution.getPath().getArcs()) {
            algorithmCost += this.solution.getInputData().getCost(arc);
            nodes.add(arc.getOrigin());
            lastArc = arc;
        }
        nodes.add(lastArc.getDestination());
        
        double pathCost = 0;
        
        // Reconstruct optimal path from the list of nodes
        if (Mode.LENGTH.equals(parameters.arcInspector.getMode())) {
            Path path = Path.createShortestPathFromNodes(parameters.graph, nodes);
            pathCost = path.getLength();

            assertEquals(this.solution.getPath().getArcs().size(), path.getArcs().size());
        }
        else if (Mode.TIME.equals(parameters.arcInspector.getMode())) {
            Path path = Path.createFastestPathFromNodes(parameters.graph, nodes);
            pathCost = path.getMinimumTravelTime();

            assertEquals(this.solution.getPath().getArcs().size(), path.getArcs().size());
        }
        
        System.err.println("mode : " + parameters.arcInspector.getMode());
        assertEquals(pathCost, algorithmCost, 1e-6);
    }





    /*
    @Test
    public void testPerformanceDijkstra() {

        ShortestPathData data =
            new ShortestPathData(graph, origin, destination, inspector);

        long start = System.nanoTime();

        ShortestPathSolution solution =
            new DijkstraAlgorithm(data).run();

        long end = System.nanoTime();

        double timeMs = (end - start) / 1e6;

        //System.out.println("Temps Dijkstra: " + timeMs);
        //System.out.println("Labels marqués: " + solution.getNbLabelsMarked());
        //System.out.println("Labels atteints: " + solution.getNbLabelsReached());



        Graph bretagneGraph = getGraph("/C:/Users/natha/Desktop/INSA/3A/BE Graphes/maps/bretagne.mapgr");


        //for (ArcInspector arcInspector : ArcInspectorFactory.getAllFilters()) {
          
            objects.add(new TestParameters(bretagneGraph, bretagneGraph.get(642480), bretagneGraph.get(619897), arcInspector, Status.INFEASIBLE));



    }*/









}