/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.linkspeed.batch;

import java.time.LocalDate;
import java.util.Objects;

import amodeus.amodeus.net.FastLinkLookup;
import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodeus.util.AmodeusTimeConvert;
import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodeus.util.math.SI;
import org.matsim.api.core.v01.network.Link;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;

public enum PathHandlerUtil {
    ;

    public static void validityCheck(TaxiTrip taxiTrip, AmodeusTimeConvert timeConvert, //
            LocalDate simulationDate, FastLinkLookup fll) {
        int tripStart = timeConvert.ldtToAmodeus(taxiTrip.pickupTimeDate, simulationDate);
        int tripEnd = timeConvert.ldtToAmodeus(taxiTrip.dropoffTimeDate, simulationDate);
        int tripDuration = tripEnd - tripStart;
        GlobalAssert.that(tripDuration >= 0);
        GlobalAssert.that(tripStart >= 0);
        GlobalAssert.that(tripEnd >= 0);
        GlobalAssert.that(!taxiTrip.pickupTimeDate.isBefore(simulationDate.atStartOfDay()));
        GlobalAssert.that(!taxiTrip.dropoffTimeDate.isBefore(simulationDate.atStartOfDay()));
        GlobalAssert.that(!taxiTrip.dropoffTimeDate.isBefore(taxiTrip.pickupTimeDate));
        GlobalAssert.that(Scalars.lessEquals(Quantity.of(0, SI.SECOND), taxiTrip.driveTime));
        Link pickupLink = fll.linkFromWGS84(taxiTrip.pickupLoc);
        Link dropOffLink = fll.linkFromWGS84(taxiTrip.dropoffLoc);
        Objects.requireNonNull(pickupLink);
        Objects.requireNonNull(dropOffLink);
    }
}
