package org.insa.graphs.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Class representing a path between nodes in a graph.
 * </p>
 * <p>
 * A path is represented as a list of {@link Arc} with an origin and not a list of
 * {@link Node} due to the multi-graph nature (multiple arcs between two nodes) of the
 * considered graphs.
 * </p>
 */
public class Path {

    /**
     * Create a new path that goes through the given list of nodes (in order), choosing
     * the fastest route if multiple are available.
     *
     * @param graph Graph containing the nodes in the list.
     * @param nodes List of nodes to build the path.
     * @return A path that goes through the given list of nodes.
     * @throws IllegalArgumentException If the list of nodes is not valid, i.e. two
     *         consecutive nodes in the list are not connected in the graph.
     */
    public static Path createFastestPathFromNodes(Graph graph, List<Node> nodes)
            throws IllegalArgumentException {
        List<Arc> arcs = new ArrayList<Arc>();

        for (int i = 0; i < nodes.size() - 1; i++) {
            Node node = nodes.get(i);
            Node nextNode = nodes.get(i + 1);

            if(!node.hasSuccessors()) {
                throw new IllegalArgumentException("Cannot create path where a node has no successor.");
            }
            
            //La liste des successeurs de node est non vide, on initialise fastest_arc avec le premier arc
            Arc fastest_arc = null;
            
            for (Arc successor : node.getSuccessors()) {

                if (successor.getDestination().equals(nextNode)) {
                    // Si fastest_arc est pas initialisé (== null) on prends le 1er arc valide
                    // Sinon on garde l'arc le plus rapide
                    // attention : avec || la seconde condition n'est pas évaluée si la 1ère est vraie
                    // ce qui est nécessaire ici sinon on pourrait apeller getMinimumTravelTime() sur un arc null
                    if (fastest_arc == null || successor.getMinimumTravelTime() < fastest_arc.getMinimumTravelTime()) {
                        fastest_arc = successor;
                    }
                }
            }
            
            if (fastest_arc == null) {
                throw new IllegalArgumentException("Cannot create path where two consecutive nodes are not connected.");
            }

            arcs.add(fastest_arc);
        }
        
        //3 cas : 1) le path contient au moins un arc, 2) le path contient un seul noeud 3) le path est vide
        if (arcs.size() > 0) {
            return new Path(graph, arcs);
        }
        if (arcs.size() == 0 && nodes.size() > 0) {
            return new Path(graph, nodes.get(0));
        }
        return new Path(graph);
    }

    /**
     * Create a new path that goes through the given list of nodes (in order), choosing
     * the shortest route if multiple are available.
     *
     * @param graph Graph containing the nodes in the list.
     * @param nodes List of nodes to build the path.
     * @return A path that goes through the given list of nodes.
     * @throws IllegalArgumentException If the list of nodes is not valid, i.e. two
     *         consecutive nodes in the list are not connected in the graph.
     */
    public static Path createShortestPathFromNodes(Graph graph, List<Node> nodes)
            throws IllegalArgumentException {
        List<Arc> arcs = new ArrayList<Arc>();

        for (int i = 0; i < nodes.size() - 1; i++) {
            Node node = nodes.get(i);
            Node nextNode = nodes.get(i + 1);

            if(!node.hasSuccessors()) {
                throw new IllegalArgumentException("Cannot create path where a node has no successor.");
            }
            
            //La liste des successeurs de node est non vide, on initialise shortest_arc avec le premier arc
            Arc shortest_arc = null;
            
            for (Arc successor : node.getSuccessors()) {

                if (successor.getDestination().equals(nextNode)) {
                    // Si shortest_arc est pas initialisé (== null) on prends le 1er arc valide
                    // Sinon on garde l'arc le plus court
                    // attention : avec || la seconde condition n'est pas évaluée si la 1ère est vraie
                    // ce qui est nécessaire ici sinon on pourrait apeller getLength() sur un arc null
                    if (shortest_arc == null || successor.getLength() < shortest_arc.getLength()) {
                        shortest_arc = successor;
                    }
                }
            }
            
            if (shortest_arc == null) {
                throw new IllegalArgumentException("Cannot create path where two consecutive nodes are not connected.");
            }

            arcs.add(shortest_arc);
        }

        //3 cas : 1) le path contient au moins un arc, 2) le path contient un seul noeud 3) le path est vide
        if (arcs.size() > 0) {
            return new Path(graph, arcs);
        }
        if (arcs.size() == 0 && nodes.size() > 0) {
            return new Path(graph, nodes.get(0));
        }
        return new Path(graph);
        
    }

    /**
     * Concatenate the given paths.
     *
     * @param paths Array of paths to concatenate.
     * @return Concatenated path.
     * @throws IllegalArgumentException if the paths cannot be concatenated (IDs of map
     *         do not match, or the end of a path is not the beginning of the next).
     */
    public static Path concatenate(Path... paths) throws IllegalArgumentException {
        if (paths.length == 0) {
            throw new IllegalArgumentException(
                    "Cannot concatenate an empty list of paths.");
        }
        final String mapId = paths[0].getGraph().getMapId();
        for (int i = 1; i < paths.length; ++i) {
            if (!paths[i].getGraph().getMapId().equals(mapId)) {
                throw new IllegalArgumentException(
                        "Cannot concatenate paths from different graphs.");
            }
        }
        ArrayList<Arc> arcs = new ArrayList<>();
        for (Path path : paths) {
            arcs.addAll(path.getArcs());
        }
        Path path = new Path(paths[0].getGraph(), arcs);
        if (!path.isValid()) {
            throw new IllegalArgumentException(
                    "Cannot concatenate paths that do not form a single path.");
        }
        return path;
    }

    // Graph containing this path.
    private final Graph graph;

    // Origin of the path
    private final Node origin;

    // List of arcs in this path.
    private final List<Arc> arcs;

    /**
     * Create an empty path corresponding to the given graph.
     *
     * @param graph Graph containing the path.
     */
    public Path(Graph graph) {
        this.graph = graph;
        this.origin = null;
        this.arcs = new ArrayList<>();
    }

    /**
     * Create a new path containing a single node.
     *
     * @param graph Graph containing the path.
     * @param node Single node of the path.
     */
    public Path(Graph graph, Node node) {
        this.graph = graph;
        this.origin = node;
        this.arcs = new ArrayList<>();
    }

    /**
     * Create a new path with the given list of arcs.
     *
     * @param graph Graph containing the path.
     * @param arcs Arcs to construct the path.
     */
    public Path(Graph graph, List<Arc> arcs) {
        this.graph = graph;
        this.arcs = arcs;
        this.origin = arcs.size() > 0 ? arcs.get(0).getOrigin() : null;
    }

    /**
     * @return Graph containing the path.
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * @return First node of the path.
     */
    public Node getOrigin() {
        return origin;
    }

    /**
     * @return Last node of the path.
     */
    public Node getDestination() {
        return arcs.get(arcs.size() - 1).getDestination();
    }

    /**
     * @return List of arcs in the path.
     */
    public List<Arc> getArcs() {
        return Collections.unmodifiableList(arcs);
    }

    /**
     * Check if this path is empty (it does not contain any node).
     *
     * @return true if this path is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.origin == null;
    }

    /**
     * Get the number of <b>nodes</b> in this path.
     *
     * @return Number of nodes in this path.
     */
    public int size() {
        return isEmpty() ? 0 : 1 + this.arcs.size();
    }

    /**
     * Check if this path is valid. A path is valid if any of the following is true:
     * <ul>
     * <li>it is empty;</li>
     * <li>it contains a single node (without arcs);</li>
     * <li>the first arc has for origin the origin of the path and, for two consecutive
     * arcs, the destination of the first one is the origin of the second one.</li>
     * </ul>
     *
     * @return true if the path is valid, false otherwise.
     */
    
    public boolean isValid() {

        // verifier le path est valide    // path est defini par des arcs
        // cas 1 : chemin vide → valide
        if (this.isEmpty()) {
            return true;
        }

        // cas 2 : un seul noeud (0 arc) → valide
        if (this.arcs.isEmpty()) {
            return true;
        }

        // vérifier que le premier arc commence bien au bon endroit
        if (!this.arcs.get(0).getOrigin().equals(this.origin)) {
            return false;
        }

        // vérifier la continuité des arcs
        for (int i = 0; i < this.arcs.size() - 1; i++) {
            Arc current = this.arcs.get(i);
            Arc next = this.arcs.get(i + 1);

            if (!current.getDestination().equals(next.getOrigin())) {
                return false;
            }
        }

        return true;
    }
    /**
     * Compute the length of this path (in meters).
     *
     * @return Total length of the path (in meters).
     */
    public float getLength() {

        float length = 0;
        for (Arc arc : this.arcs) {
            length += arc.getLength();
        }
        return length;
    }

    /**
     * Compute the time required to travel this path if moving at the given speed.
     *
     * @param speed Speed to compute the travel time.
     * @return Time (in seconds) required to travel this path at the given speed (in
     *         kilometers-per-hour).
     */
    public double getTravelTime(double speed) {
        double total = 0;
        for (Arc arc : this.arcs) {
            total += arc.getTravelTime(speed);
        }
        return total;
    }

    /**
     * Compute the time to travel this path if moving at the maximum allowed speed on
     * every arc.
     *
     * @return Minimum travel time to travel this path (in seconds).
     */
    public double getMinimumTravelTime() {
        double total = 0;
        for (Arc arc : this.arcs) {
            total += arc.getMinimumTravelTime();
        }
        return total;
    }

}
