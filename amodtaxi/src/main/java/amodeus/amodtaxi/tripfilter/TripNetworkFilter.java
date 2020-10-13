/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.tripfilter;

import amodeus.amodeus.net.MatsimAmodeusDatabase;
import amodeus.amodeus.taxitrip.ShortestDurationCalculator;
import amodeus.amodeus.taxitrip.TaxiTrip;
import org.matsim.api.core.v01.network.Network;

import amodeus.amodtaxi.linkspeed.iterative.DurationCompare;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** This filter calculates the min-time-path in the network without traffic.
 * Then, only trips are kept which:
 * - are slower than the network allows (speeding etc.)
 * - have less delay w.r.t. free speed than maxDelay
 * - have an average speed faster than minSpeed (endless waiting, leaving taxi meter on, ...)
 * - are longer than a minimum distance
 * - have a nontrivial path of more than 1 link */
public class TripNetworkFilter extends AbstractConsciousFilter {
    private final ShortestDurationCalculator calc;
    private final Scalar maxDelay;
    private final Scalar minSpeed;
    private final Scalar minDistance;
    private final boolean checkSlowerNetwork;

    private int numslowerThanNetwork = 0;
    private int numbelowMaxDelay = 0;
    private int numfasterThanMinSpeed = 0;
    private int numlongerThanMinDistance = 0;
    private int numhasRealPath = 0;
    private Tensor ratios = Tensors.empty();
    private Tensor durations = Tensors.empty();

    public TripNetworkFilter(Network network, MatsimAmodeusDatabase db, //
            Scalar minSpeed, Scalar maxDelay, Scalar minDistance, boolean checkSlowerNetwork) {
        calc = new ShortestDurationCalculator(network, db);
        this.maxDelay = maxDelay;
        this.minSpeed = minSpeed;
        this.minDistance = minDistance;
        this.checkSlowerNetwork = checkSlowerNetwork;
    }

    @Override // from AbstractConsciousFilter
    public boolean testInternal(TaxiTrip trip) {
        /** getting the data */
        DurationCompare compare = new DurationCompare(trip, calc);

        /** evaluating criteria */
        boolean slowerThanNetwork = !checkSlowerNetwork || Scalars.lessEquals(compare.nwPathDurationRatio, RealScalar.ONE);
        if (slowerThanNetwork)
            ++numslowerThanNetwork;
        boolean belowMaxDelay = Scalars.lessEquals(trip.driveTime.subtract(compare.pathTime), maxDelay);
        if (belowMaxDelay)
            ++numbelowMaxDelay;
        boolean fasterThanMinSpeed = Scalars.lessEquals(minSpeed, compare.pathDist.divide(trip.driveTime));
        if (fasterThanMinSpeed)
            ++numfasterThanMinSpeed;
        boolean longerThanMinDistance = Scalars.lessEquals(minDistance, compare.pathDist);
        if (longerThanMinDistance)
            ++numlongerThanMinDistance;
        boolean hasRealPath = compare.path.links.size() > 1;
        if (hasRealPath)
            ++numhasRealPath;

        if (!slowerThanNetwork) {
            ratios.append(compare.nwPathDurationRatio);
            durations.append(compare.duration);
        }

        /** return true if all ok */
        return slowerThanNetwork && belowMaxDelay && fasterThanMinSpeed && longerThanMinDistance && hasRealPath;
    }

    @Override
    public void printSummary() {
        System.out.println("Filter:                   " + this.getClass().getSimpleName());
        System.out.println("Total filtered:           " + numTested());
        System.out.println("Slower than network:      " + numslowerThanNetwork + " / " + numTested());
        System.out.println("Below maximum delay:      " + numbelowMaxDelay + " / " + numTested());
        System.out.println("Faster than min speed:    " + numfasterThanMinSpeed + " / " + numTested());
        System.out.println("Longer than min distance: " + numlongerThanMinDistance + " / " + numTested());
        System.out.println("Have real path:           " + numhasRealPath + " / " + numTested());
        // try {
        //     SaveUtils.saveFile(ratios, "ratios", HomeDirectory.file("data/TaxiComparison_SFScenario"));
        //     UnitSaveUtils.saveFile(durations, "durations", HomeDirectory.file("data/TaxiComparison_SFScenario"));
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
    }
}
