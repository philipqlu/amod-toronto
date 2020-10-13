/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.trace;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/** A {@link DayTaxiRecord} contains the {@link TaxiTrail} of all taxis in the dataset, first
 * fill with data using the insert function, then postprocess. Every trail is postprocessed independently.
 * 
 * @author clruch */
public interface DayTaxiRecord {
    /** part 1: filling with data */
    /** @param list timestamp in data */
    void insert(List<String> list, int taxiNumber, String id);

    /** part 2: postprocess trails once filled */
    void processFilledTrails() throws Exception;

    /** part 3: access functions */
    TaxiTrail get(int vehicleIndex);

    int numTaxis();

    /** @return oldest {@link LocalDateTime} in all {@link TaxiTrail}s of the {@link DayTaxiRecord} */
    LocalDateTime getMaxTime();

    Collection<TaxiTrail> getTrails();
}
