/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.tripmodif;

import java.io.File;

import amodeus.amodtaxi.tripfilter.TaxiTripFilterCollection;

@FunctionalInterface
public interface TaxiDataModifier {

    /** @return a new {@link File} containing taxi trip data, which is derived
     *         from the original {@link File} @param taxiData. Modifications are
     *         necessary changes in the data to produce meaningful scenarios, e.g.,
     *         - distributing trips deparing in 15 minute intervals in these intervals
     *         - distributing trips in geographical areas, e.g., if only the departure zone is known
     * 
     *         Filtering, i.e., removing certain trips according to some criteria is done
     *         with a {@link TaxiTripFilterCollection} and not with classes implementing this interface.
     * 
     * @throws Exception */
    File modify(File taxiData) throws Exception;

}
