/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.trace;

import java.io.File;

@FunctionalInterface
public interface CsvFleetReaderInterface {

    /** @param trail a csv file with taxi journey information
     * @param taxiNumber the number of the taxi
     * @return {@link DayTaxiRecord} in which all of the informations in the file are loaded
     * @throws Exception */
    DayTaxiRecord populateFrom(File trail, int taxiNumber);
}
