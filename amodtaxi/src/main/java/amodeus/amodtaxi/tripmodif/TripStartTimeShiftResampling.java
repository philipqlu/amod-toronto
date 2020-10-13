/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.tripmodif;

import java.time.LocalDateTime;
import java.util.Random;

import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodeus.util.LocalDateTimes;
import amodeus.amodeus.util.math.GlobalAssert;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

// TODO RVM document
public class TripStartTimeShiftResampling implements TripModifier {

    private final Random random;
    private final Scalar maxShift;

    public TripStartTimeShiftResampling(Random random, Scalar maxShift) {
        this.random = random;
        this.maxShift = maxShift;
    }

    @Override
    public TaxiTrip modify(TaxiTrip taxiTrip) {

        /** get start time and duration */
        LocalDateTime start = taxiTrip.pickupTimeDate;

        /** assert that start time is multiple of 15 minutes */
//        GlobalAssert.that(start.getMinute() % 15 == 0); // this is always true for Chicago online data

        /** compute a random time shift */
        Scalar shift = RealScalar.of(random.nextDouble()).multiply(maxShift);

        /** compute updated trip and return */
        return TaxiTrip.of( //
                taxiTrip.localId, //
                taxiTrip.taxiId, //
                taxiTrip.pickupLoc, //
                taxiTrip.dropoffLoc, //
                taxiTrip.distance, //
                LocalDateTimes.addTo(taxiTrip.pickupTimeDate, shift), //
                taxiTrip.waitTime, //
                taxiTrip.driveTime);
    }

    @Override
    public void notify(TaxiTrip taxiTrip) {
        // -- deliberately empty
    }
}