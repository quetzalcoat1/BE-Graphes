package org.insa.graphs.algorithm.utils;

import org.insa.graphs.algorithm.shortestpath.AStarAlgorithm;
import org.insa.graphs.algorithm.shortestpath.ShortestPathAlgorithm;
import org.insa.graphs.algorithm.shortestpath.ShortestPathData;

public class AStarAlgorithmTest extends ShortestPathAlgorithmTest {
    
    @Override
    public ShortestPathAlgorithm createAlgorithm(ShortestPathData data) {
        return new AStarAlgorithm(data);
    }
}