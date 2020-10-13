/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.linkspeed.iterative;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

import amodeus.amodeus.dispatcher.util.FIFOFixedQueue;
import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodeus.util.math.GlobalAssert;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class RandomTripMaintainer {
    /** random generator */
    private final Random random;
    /** maintaining a list of last recorded ratios */
    private final FIFOFixedQueue<Scalar> lastRatios;
    /** number of trips used for convergence assessment */
    private final int checkHorizon;
    /** cost function for convergence check */
    private final Function<List<Scalar>, Scalar> costFunction;
    /** map tracking number of queries; Integer denotes times the trips was queried, so all trips start with key 0 */
    private final NavigableMap<Integer, Set<TaxiTrip>> queryMap = new TreeMap<>();
    private final int numTrips;

    public RandomTripMaintainer(List<TaxiTrip> allTrips, int checkHorizon, //
            Function<List<Scalar>, Scalar> costFunction, Random random) {
        lastRatios = new FIFOFixedQueue<>(allTrips.size());
        this.checkHorizon = checkHorizon > allTrips.size() ? allTrips.size() : checkHorizon;
        this.costFunction = costFunction;
        this.random = random;
        numTrips = allTrips.size();

        /** fill query map */
        Collections.shuffle(allTrips, random);
        queryMap.put(0, new HashSet<TaxiTrip>(allTrips));
        GlobalAssert.that(allTrips.size() == queryMap.firstEntry().getValue().size());
    }

    /** queries next trip, trip is removed from list with lowest key in querymap(least amount of queries) and put in list with key+1
     *
     * @return queried {@link TaxiTrip} */
    public TaxiTrip nextRandom() {
        // find set of trips with least checks
        int numChecks = 0;
        while (!(queryMap.ceilingEntry(numChecks).getValue().size() > 0))
            ++numChecks;

        Set<TaxiTrip> leastChecked = queryMap.ceilingEntry(numChecks).getValue();

        // select random trip from it
        TaxiTrip selected = leastChecked.stream().skip(random.nextInt(leastChecked.size())).findFirst().get();
        // move to set with +1 checks
        GlobalAssert.that(leastChecked.remove(selected));
        if (queryMap.lastKey() == numChecks)
            queryMap.put(numChecks + 1, new HashSet<>());
        queryMap.ceilingEntry(numChecks + 1).getValue().add(selected);

        // return
        return selected;
    }

    public void addRecordedRatio(Scalar ratio) {
        lastRatios.manage(ratio);
    }

    public Scalar getRatioCost() {
        return costFunction.apply(lastRatios.getNewest(checkHorizon));
    }

    public int numTrips() {
        return numTrips;
    }

    public Tensor getRatios() {
        return Tensor.of(lastRatios.getNewest(checkHorizon).stream());
    }
}
