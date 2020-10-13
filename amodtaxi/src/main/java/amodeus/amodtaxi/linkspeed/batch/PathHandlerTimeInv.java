/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.linkspeed.batch;

import java.util.ArrayList;
import java.util.List;

import amodeus.amodeus.taxitrip.ShortestDurationCalculator;
import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodeus.util.math.SI;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ class PathHandlerTimeInv {
    public final Scalar duration;
    public final Scalar freeflowDuation;
    public final List<Link> travelledLinks;
    private final boolean isValid;

    public PathHandlerTimeInv(TaxiTrip taxiTrip, ShortestDurationCalculator calc) {
        /** compute fastest path */
        Path fastest = calc.computePath(taxiTrip);

        /** extract data from {@link TaxiTrip} */
        this.duration = taxiTrip.driveTime;
        /** extract data from free flow shortest path */
        this.freeflowDuation = Quantity.of(fastest.travelTime, SI.SECOND);

        travelledLinks = new ArrayList<>(fastest.links);

        isValid = Scalars.lessEquals(freeflowDuation, duration);
    }

    public boolean isValid() {
        return isValid;
    }
}
