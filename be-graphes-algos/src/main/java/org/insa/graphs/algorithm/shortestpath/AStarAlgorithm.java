package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Point;

public class AStarAlgorithm extends DijkstraAlgorithm {

    public AStarAlgorithm(ShortestPathData data) {
        super(data);
    }

    @Override
    public LabelStar initialisation(int i, Graph graph, ShortestPathData data) {
        return new LabelStar(i, Point.distance(graph.get(i).getPoint(), data.getDestination().getPoint()));
    }
}
