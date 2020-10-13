package amodeus.amodtaxi.tripmodif;

import amodeus.amodeus.taxitrip.TaxiTrip;

public class RenumerationModifier implements TripModifier {
    private int localId = 0;

    @Override
    public TaxiTrip modify(TaxiTrip taxiTrip) {
        return TaxiTrip.of( //
                String.valueOf(localId++), //
                taxiTrip.taxiId, //
                taxiTrip.pickupLoc, //
                taxiTrip.dropoffLoc, //
                taxiTrip.distance, //
                taxiTrip.pickupTimeDate, //
                taxiTrip.waitTime, //
                taxiTrip.driveTime);
    }

    @Override
    public void notify(TaxiTrip taxiTrip) {
        // -- deliberately empty
    }
}
