/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.trace;

import java.io.File;
import java.util.Collection;

import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodtaxi.scenario.Consistency;

public enum ReadTraceFiles {
    ;

    public static DayTaxiRecord in(Collection<File> trcFls, CsvFleetReaderInterface reader) throws Exception {
        /** part 1: filling with data */
        GlobalAssert.that(!trcFls.isEmpty());
        DayTaxiRecord dayTaxiRecord = null;
        int taxiNum = 0;
        for (File trc : trcFls) {
            System.out.println("Now processing: " + trc.getName());
            dayTaxiRecord = reader.populateFrom(trc, taxiNum += 1);
        }

        /** part 2: postprocess trails once filled */
        dayTaxiRecord.processFilledTrails();
        Consistency.checkTrail(dayTaxiRecord.getTrails());
        return dayTaxiRecord;
    }
}
