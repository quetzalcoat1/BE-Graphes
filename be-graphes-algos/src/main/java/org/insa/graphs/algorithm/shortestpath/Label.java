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
        return this.sommetCourant;
    }

    public boolean isMarque() {
        return this.marque;
    }

    public double getCoutRealise() {
        return this.coutRealise;
    }

    public double getTotalCost() {
        return getCoutRealise();
    }

    public Arc getPere() {
        return this.pere;
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

    public double getCoutEstime() {
        return 0;
    }

    @Override
    public int compareTo(Label other) {
        int compare = Double.compare(this.getTotalCost(), other.getTotalCost());

        if (compare == 0) {
            return Double.compare(this.getCoutEstime(), other.getCoutEstime());
        }

        return compare;
    }
}
