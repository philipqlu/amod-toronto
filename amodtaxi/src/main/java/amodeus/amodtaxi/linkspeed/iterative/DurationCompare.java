/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.linkspeed.iterative;

import amodeus.amodeus.taxitrip.ShortestDurationCalculator;
import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodeus.util.math.SI;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** Helper class to compare the duration of a taxi trip (from live data)
 * and a network path. If the nwPathdurationRatio is larger than 1,
 * the trip is slower in the simulatio network than in the original data. */
public class DurationCompare {
    public final Path path;
    public final Scalar duration;
    public final Scalar pathTime;
    public final Scalar pathDist;
    /** =1 simulation duration identical t.recorded duration
     * < 1 simulation duration faster than recorded duration
     * > 1 simulation duration slower than recorded duration */
    public final Scalar nwPathDurationRatio;

    public DurationCompare(TaxiTrip trip, ShortestDurationCalculator calc) {
        path = calc.computePath(trip);
        pathTime = Quantity.of(path.travelTime, SI.SECOND);
        pathDist = Quantity.of(path.links.stream().mapToDouble(Link::getLength).sum(), SI.METER);
        duration = trip.driveTime;
        nwPathDurationRatio = pathTime.divide(duration);
    }
}
