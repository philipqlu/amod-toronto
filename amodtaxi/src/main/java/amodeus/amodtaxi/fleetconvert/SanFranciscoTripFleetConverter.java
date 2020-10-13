package amodeus.amodtaxi.fleetconvert;

import java.io.File;
import java.time.LocalDate;

import amodeus.amodeus.options.ScenarioOptions;
import amodeus.amodeus.util.math.SI;
import amodeus.amodtaxi.scenario.TaxiTripsSuppliers;
import amodeus.amodtaxi.scenario.chicago.ScenarioConstants;
import amodeus.amodtaxi.trace.DayTaxiRecord;
import amodeus.amodtaxi.tripfilter.TaxiTripFilterCollection;
import amodeus.amodtaxi.tripfilter.TripDurationFilter;
import amodeus.amodtaxi.tripfilter.TripEndTimeFilter;
import amodeus.amodtaxi.tripmodif.TaxiDataModifier;
import ch.ethz.idsc.tensor.qty.Quantity;
import org.matsim.api.core.v01.network.Network;

public class SanFranciscoTripFleetConverter extends TripFleetConverter {
    public SanFranciscoTripFleetConverter(ScenarioOptions scenarioOptions, Network network, //
            DayTaxiRecord dayTaxiRecord, LocalDate localDate, //
            TaxiDataModifier modifier, TaxiTripFilterCollection finalFilters, //
            File targetDirectory) {
        super(scenarioOptions, network, modifier, finalFilters, TaxiTripsSuppliers.fromDayTaxiRecord(dayTaxiRecord, localDate), targetDirectory);
    }

    @Override
    public void setFilters() {
        /** trips outside the range [150[s], 30[h]] are removed */
        primaryFilter.addFilter(new TripDurationFilter(Quantity.of(150, SI.SECOND), Quantity.of(10800, SI.SECOND)));

        /** trips which end after the maximum end time are rejected */
        primaryFilter.addFilter(new TripEndTimeFilter(ScenarioConstants.maxEndTime));
    }
}
