/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.tripfilter;

import java.util.function.Predicate;

import amodeus.amodeus.taxitrip.TaxiTrip;

/* package */ interface ConsciousFilter extends Predicate<TaxiTrip> {

    /** @return number of assessments made */
    int numTested();

    /** @return number of assessments labeled false */
    int numFalse();

    /** print a summary to console */
    void printSummary();
}
