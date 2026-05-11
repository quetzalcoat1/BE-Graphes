package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Arc;

public class Label implements Comparable<Label> {

    private int sommetCourant;
    private boolean marque;
    private double coutRealise;
    private Arc pere;

    public Label(int sommetCourant) {
        this.sommetCourant = sommetCourant;
        this.marque = false;
        this.coutRealise = Double.POSITIVE_INFINITY;
        this.pere = null;
    }

    // Getters
    public int getSommetCourant() {
        return sommetCourant;
    }

    public boolean isMarque() {
        return marque;
    }

    public double getCoutRealise() {
        return coutRealise;
    }

    public Arc getPere() {
        return pere;
    }

    // Setter marque
    public void setMarque(boolean marque) {
        this.marque = marque;
    }

    // Setter coût
    public void setCoutRealise(double coutRealise) {
        this.coutRealise = coutRealise;
    }

    // Setter père
    public void setPere(Arc pere) {
        this.pere = pere;
    }

    public double getCost() {
        return coutRealise;
    }


    @Override
    public int compareTo(Label other) {
        return Double.compare(this.getCost(), other.getCost());
    }
}
