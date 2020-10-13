package amodeus.amodtaxi.scenario;

import java.io.File;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodtaxi.trace.CsvFleetReaderInterface;
import amodeus.amodtaxi.trace.DayTaxiRecord;
import amodeus.amodtaxi.trace.ReadTraceFiles;
import amodeus.amodtaxi.tripmodif.NullModifier;
import amodeus.amodtaxi.tripmodif.TaxiDataModifier;
import amodeus.amodtaxi.util.NamingConvention;
import org.apache.commons.io.FileUtils;

public class TaxiTripsSuppliers {
    public static TaxiTripsSupplier fromReader(File tripFile, File targetDirectory, TaxiTripsReader tripsReader) {
        return fromReader(tripFile, targetDirectory, tripsReader, NullModifier.INSTANCE);
    }

    public static TaxiTripsSupplier fromReader(File tripFile, File targetDirectory, TaxiTripsReader tripsReader, TaxiDataModifier modifier) {
        return new TaxiTripsSupplier() {
            @Override
            public Collection<TaxiTrip> get() {
                try {
                    /** folder for processing stored files, the folder tripData contains
                     * .csv versions of all processing steps for faster debugging. */
                    FileUtils.copyFileToDirectory(tripFile, targetDirectory);
                    File newTripFile = new File(targetDirectory, tripFile.getName());
                    System.out.println("NewTripFile: " + newTripFile.getAbsolutePath());
                    GlobalAssert.that(newTripFile.isFile());

                    /** initial formal modifications, e.g., replacing certain characters,
                     * other modifications should be done in the third step */
                    File preparedFile = modifier.modify(newTripFile);

                    /** save unreadable trips for post-processing, checking */
                    File unreadable = new File(preparedFile.getParentFile(), NamingConvention.similarTo(preparedFile).apply("unreadable"));
                    tripsReader.saveUnreadable(unreadable);

                    return tripsReader.getTrips(preparedFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    return Collections.emptyList();
                }
            }
        };
    }

    public static TaxiTripsSupplier fromTraceFiles(Collection<File> trcFls, CsvFleetReaderInterface reader, LocalDate localDate) throws Exception {
        return fromDayTaxiRecord(ReadTraceFiles.in(trcFls, reader), localDate);
    }

    public static TaxiTripsSupplier fromDayTaxiRecord(DayTaxiRecord dayTaxiRecord, LocalDate localDate) {
        return () -> AllTaxiTrips.in(dayTaxiRecord).on(localDate);
    }
}
