/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.sanfrancisco;

import java.time.ZoneId;
import java.util.List;

import amodeus.amodeus.util.AmodeusTimeConvert;
import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodtaxi.scenario.data.TaxiStampReader;
import org.matsim.api.core.v01.Coord;

import amodeus.amodtaxi.trace.TaxiStamp;

public enum TaxiStampReaderSF implements TaxiStampReader {
    INSTANCE;

    private static final AmodeusTimeConvert TIME_CONVERT = new AmodeusTimeConvert(ZoneId.of("America/Los_Angeles"));

    /** one entry in an SF data file is [lat long occ unixEpochTime],
     * e.g., [37.79655 -122.39521 1 1213035317] */
    @Override
    public TaxiStamp read(List<String> dataRow) {
        GlobalAssert.that(dataRow.size() == 4);
        TaxiStamp taxiStamp = new TaxiStamp();

        /** time */
        taxiStamp.globalTime = TIME_CONVERT.getLdt(Integer.parseInt(dataRow.get(3)));

        /** occupancy status */
        taxiStamp.occupied = Integer.parseInt(dataRow.get(2)) == 1;

        /** coordinate */
        taxiStamp.gps = new Coord( //
                Double.parseDouble(dataRow.get(1)), //
                Double.parseDouble(dataRow.get(0)));
        return taxiStamp;
    }

    @Override
    public AmodeusTimeConvert timeConvert() {
        return TIME_CONVERT;
    }
}
