package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.algorithm.AbstractInputData.Mode;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Point;

public class AStarAlgorithm extends DijkstraAlgorithm {

    public AStarAlgorithm(ShortestPathData data) {
        super(data);
    }

    @Override
    public LabelStar initialisationLabel(int i, Graph graph, ShortestPathData data) {

        if (data.getMode() == Mode.LENGTH) {
            return new LabelStar(i, Point.distance(graph.get(i).getPoint(), data.getDestination().getPoint()));
        }
        else {  // This is the case when (data.getMode() == Mode.TIME), if another Mode is created, we need to change here
            return new LabelStar(i, (double) (Point.distance(graph.get(i).getPoint(), data.getDestination().getPoint()) / (double) (data.getGraph().getGraphInformation().getMaximumSpeed()*1000))*3600);
        }
    }
}
