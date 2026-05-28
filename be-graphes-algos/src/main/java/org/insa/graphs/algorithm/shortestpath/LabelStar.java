package org.insa.graphs.algorithm.shortestpath;

public class LabelStar extends Label {

    double coutEstime = Double.POSITIVE_INFINITY;

    public LabelStar(int sommetCourant, double coutEstime) {
        super(sommetCourant);
        this.coutEstime = coutEstime;
    }

    @Override
    public double getCoutEstime() {
        return this.coutEstime;
    }

    @Override
    public double getTotalCost() {
        return getCoutRealise() + this.coutEstime;
    }

}
