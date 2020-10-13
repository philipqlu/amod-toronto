/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.tripfilter;

import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodeus.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;

/** Filter used to remove {@link TaxiTrip}s which a duration not in the
 * interval [minDuration,maxDuration] */
public class TripDurationFilter extends AbstractConsciousFilter {
    private final Scalar minDuration;
    private final Scalar maxDuration;

    public TripDurationFilter(Scalar minDuration, Scalar maxDuration) {
        GlobalAssert.that(Scalars.lessEquals(Quantity.of(0, SI.SECOND), minDuration));
        GlobalAssert.that(Scalars.lessEquals(Quantity.of(0, SI.SECOND), maxDuration));
        GlobalAssert.that(Scalars.lessEquals(minDuration, maxDuration));
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
    }

    @Override
    public boolean testInternal(TaxiTrip t) {
        return Scalars.lessEquals(minDuration, t.driveTime) && //
                Scalars.lessEquals(t.driveTime, maxDuration);
    }
}
