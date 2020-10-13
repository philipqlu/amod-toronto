/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodtaxi.trace.DayTaxiRecord;

public class AllTaxiTrips {

    /** Usage: {@link Collection}<{@link TaxiTrip}> trips = AllTaxiTrips.in(dayTaxiRecord).on(simulationDate)
     * 
     * @return all {@link TaxiTrip}s found in the {@link DayTaxiRecord}
     * @param dayTaxiRecord with trip start on a certain {@link LocalDate} */
    public static AllTaxiTrips in(DayTaxiRecord dayTaxiRecord) {
        return new AllTaxiTrips(dayTaxiRecord);
    }

    // --
    private final DayTaxiRecord dayTaxiRecord;

    private AllTaxiTrips(DayTaxiRecord dayTaxiRecord) {
        this.dayTaxiRecord = dayTaxiRecord;
    }

    public Collection<TaxiTrip> on(LocalDate simulationDate) {
        return dayTaxiRecord.getTrails().stream().flatMap(trail -> trail.allTripsBeginningOn(simulationDate).stream()).collect(Collectors.toList());
    }
}
