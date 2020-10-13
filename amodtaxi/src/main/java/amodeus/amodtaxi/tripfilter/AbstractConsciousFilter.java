/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.tripfilter;

import amodeus.amodeus.taxitrip.TaxiTrip;

/* package */ abstract class AbstractConsciousFilter implements ConsciousFilter {

    protected int numTested = 0;
    protected int numFalse = 0;

    /** @param t
     * @return */
    protected abstract boolean testInternal(TaxiTrip t);

    @Override
    public final boolean test(TaxiTrip t) {
        ++numTested;
        boolean accepted = testInternal(t);
        if (!accepted)
            ++numFalse;
        return accepted;
    }

    @Override
    public final int numTested() {
        return numTested;
    }

    @Override
    public final int numFalse() {
        return numFalse;
    }

    @Override
    public void printSummary() {
        System.out.println("Filter:        " + this.getClass().getSimpleName());
        System.out.println("Number tested: " + numTested);
        System.out.println("Number false:  " + numFalse);
    }
}
