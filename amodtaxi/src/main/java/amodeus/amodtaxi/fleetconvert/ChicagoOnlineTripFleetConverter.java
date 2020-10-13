/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.fleetconvert;

import java.io.File;
import java.util.Random;

import amodeus.amodeus.options.ScenarioOptions;
import amodeus.amodeus.util.math.SI;
import amodeus.amodtaxi.scenario.TaxiTripsSuppliers;
import org.matsim.api.core.v01.network.Network;

import amodeus.amodtaxi.scenario.TaxiTripsReader;
import amodeus.amodtaxi.scenario.chicago.ScenarioConstants;
import amodeus.amodtaxi.tripfilter.RandomRemoverFilter;
import amodeus.amodtaxi.tripfilter.TaxiTripFilterCollection;
import amodeus.amodtaxi.tripfilter.TripDurationFilter;
import amodeus.amodtaxi.tripfilter.TripEndTimeFilter;
import amodeus.amodtaxi.tripmodif.TaxiDataModifier;
import ch.ethz.idsc.tensor.qty.Quantity;

public class ChicagoOnlineTripFleetConverter extends TripFleetConverter {

    public ChicagoOnlineTripFleetConverter(ScenarioOptions scenarioOptions, Network network, //
            TaxiDataModifier modifier, TaxiDataModifier generalModifier, TaxiTripFilterCollection finalFilters, //
            TaxiTripsReader tripsReader, File tripFile, File targetDirectory) {
        super(scenarioOptions, network, modifier, finalFilters, TaxiTripsSuppliers.fromReader(tripFile, targetDirectory, tripsReader, generalModifier), targetDirectory);
    }

    @Override
    public void setFilters() {
        /** trips outside the range [150[s], 30[h]] are removed */
        primaryFilter.addFilter(new TripDurationFilter(Quantity.of(150, SI.SECOND), Quantity.of(10800, SI.SECOND)));

        /** trips which end after the maximum end time are rejected */
        primaryFilter.addFilter(new TripEndTimeFilter(ScenarioConstants.maxEndTime));

        /** removes a percentage of trips randomly, only used for debugging, for full
         * scale a value > 1.0 is used in the second argument. */
        primaryFilter.addFilter(new RandomRemoverFilter(new Random(123), 1.5));

        // TODO add this again if necessary, otherwise remove eventually and delete classes...
        /** trips which are only explainable with speeds well above 85 miles/hour are removed */
        // primaryFilter.addFilter(new TripMaxSpeedFilter(network, db, ScenarioConstants.maxAllowedSpeed));

        // others
        // cleaner.addFilter(new TripStartTimeResampling(15)); // start/end times in 15 min resolution
        // cleaner.addFilter(new TripEndTimeCorrection());
        // cleaner.addFilter(new TripNetworkFilter(scenarioOptions, network));
        // cleaner.addFilter(new TripDistanceRatioFilter(4)); // massive slow down
        // cleaner.addFilter(new TripDistanceFilter(Quantity.of(500, SI.METER), Quantity.of(50000, SI.METER)));
    }

}
