/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.trace;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.NavigableMap;

import amodeus.amodeus.taxitrip.TaxiTrip;

/** A TaxiTrail contains a sorted map <Integer, TaxiStamp> with all the
 * {@link TaxiStamp} of the dataset.
 * 
 * @author clruch */
public interface TaxiTrail {

    /** part 1: filling with data
     * 
     * @param list of data file for a certain time */
    void insert(List<String> list);

    /** part 2: processing of the data */
    void processFilledTrail() throws Exception;

    /** part 3: access functions */
    LocalDateTime getMaxTime();

    NavigableMap<LocalDateTime, TaxiStamp> getTaxiStamps();

    String getID();

    /** @return {@link Collection} of all {@link TaxiTrip}s with
     *         pickup taking place on {@link LocalDate} @param localDate */
    Collection<TaxiTrip> allTripsBeginningOn(LocalDate localDate);

}
