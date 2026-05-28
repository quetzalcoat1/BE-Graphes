package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.Collections;

import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Path;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }

    public Label initialisation(int i, Graph graph, ShortestPathData data) {
        return new Label(i);
    }

    @Override
    protected ShortestPathSolution doRun() {

        // retrieve data from the input problem (getInputData() is inherited from the
        // parent class ShortestPathAlgorithm)
        final ShortestPathData data = getInputData();

        // simple checks to verify the existence of input data
        if (data == null) {
            return new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        else if (data.getGraph() == null || data.getOrigin() == null || data.getDestination() == null) {
            return new ShortestPathSolution(data, Status.INFEASIBLE);
        } 

        Graph graph = data.getGraph();

        final int nbNodes = graph.size();


        Label[] labels = new Label[nbNodes];
        for (int i = 0; i < nbNodes; ++i) { labels[i] = initialisation(i, graph, data); }

        BinaryHeap<Label> tasLabel = new BinaryHeap<Label>();
        
        labels[data.getOrigin().getId()].setCoutRealise(0);
        tasLabel.insert(labels[data.getOrigin().getId()]);

        // Notify observers about the first event (origin processed).
        notifyOriginProcessed(data.getOrigin());

        //for (i = 0; i < nbNodes && !tasLabel.isEmpty(); ++i) {
        while (!tasLabel.isEmpty()) {

            // Choose label with minimum cost and remove it from the heap
            int idSommetCourant = tasLabel.deleteMin().getSommetCourant();
            labels[idSommetCourant].setMarque(true);
            notifyNodeMarked(graph.get(idSommetCourant));

            if (idSommetCourant == data.getDestination().getId()) {
                break;
            }

            // Iterate over the successors of the current node and update their labels
            for (Arc arc : graph.get(idSommetCourant).getSuccessors()) {

                if (!data.isAllowed(arc)) { continue; }

                int idSuccesseur = arc.getDestination().getId();
                
                // If the node is already marked, we do not need to update its label
                if (labels[idSuccesseur].isMarque()) { continue; }
                
                // Retrieve weight of the arc.
                double w = data.getCost(arc);
                double ancienCoutRealise  = labels[idSuccesseur].getCoutRealise();
                double nouveauCoutRealise = labels[idSommetCourant].getCoutRealise() + w;
                
                if (Double.isInfinite(ancienCoutRealise) && Double.isFinite(nouveauCoutRealise)) {
                    notifyNodeReached(arc.getDestination());
                }

                if (nouveauCoutRealise < ancienCoutRealise) {

                    if (ancienCoutRealise != Double.POSITIVE_INFINITY) {
                        // If the label was already in the heap, we need to remove it to update its position
                        tasLabel.remove(labels[idSuccesseur]);
                    }

                    labels[idSuccesseur].setCoutRealise(nouveauCoutRealise);
                    labels[idSuccesseur].setPere(arc);

                    tasLabel.insert(labels[idSuccesseur]);
                    
                }
            }
        }

        // Variable that will contain the solution of the shortest path problem
        ShortestPathSolution solution = null;

        // Destination has no predecessor, the solution is infeasible...
        if (labels[data.getDestination().getId()].getPere() == null) {
            solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        else {
            // The destination has been found, notify the observers.
            notifyDestinationReached(data.getDestination());

            // Create the path from the array of predecessors...
            ArrayList<Arc> arcs = new ArrayList<>();
            Arc arc = labels[data.getDestination().getId()].getPere();
            while (arc != null) {
                arcs.add(arc);
                arc = labels[arc.getOrigin().getId()].getPere();
            }

            // Reverse the path...
            Collections.reverse(arcs);

            // Create the final solution.
            Path path = new Path(graph, arcs);

            solution = new ShortestPathSolution(data, Status.OPTIMAL, path);
        }

        return solution;
    }

}
