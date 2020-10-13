/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodeus.util.Duration;
import amodeus.amodeus.util.LocalDateTimes;
import amodeus.amodeus.util.io.CsvReader;
import amodeus.amodeus.util.io.CsvReader.Row;
import amodeus.amodeus.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

public abstract class TaxiTripsReader {
    private final String delim;
    private final Map<String, Integer> taxiIds = new HashMap<>();
    private final List<String> unreadable = new ArrayList<>();

    public TaxiTripsReader(String delim) {
        this.delim = delim;
    }

    public List<TaxiTrip> getTrips(File file) throws IOException {
        final AtomicInteger tripIds = new AtomicInteger(0);
        List<TaxiTrip> list = new LinkedList<>();
        System.out.println("TaxiTripsReader, reading file: " + file.getAbsolutePath());
        CsvReader reader = new CsvReader(file, delim);
        unreadable.add(String.join(",", reader.sortedHeaders()));
        reader.rows(row -> {
            int incrm = tripIds.getAndIncrement();
            String tripId = "no_dat_id_" + Integer.toString(incrm);
            if (incrm % 1000 == 0)
                System.out.println("trips: " + tripId);
            try {
                String taxiCode = getTaxiId(row);
                int taxiId = taxiIds.getOrDefault(taxiCode, taxiIds.size());
                taxiIds.put(taxiCode, taxiId);
                LocalDateTime pickupTime = getPickupTime(row);
                LocalDateTime dropoffTime = getDropoffTime(row);
                Scalar waitingTime = getWaitingTime(row);
                LocalDateTime submissionTimeDate = Objects.nonNull(waitingTime) ? LocalDateTimes.subtractFrom(pickupTime, waitingTime) : null;
                Scalar durationCompute = Duration.between(pickupTime, dropoffTime);
                Scalar durationDataset = getDuration(row);
                String dataTripId = getTripId(row);
                if (Objects.nonNull(dataTripId))
                    tripId = dataTripId;
                if (Scalars.lessEquals(Quantity.of(0.1, SI.SECOND), //
                        durationDataset.subtract(durationCompute).abs()))
                    System.err.println("Mismatch between duration recorded in data" + //
                    "and computed duration," + //
                    "computed duration using start and end time: " + //
                    pickupTime + " --> " + dropoffTime + " != " + durationDataset);
                TaxiTrip trip = TaxiTrip.of(//
                        tripId, //
                        Integer.toString(taxiId), //
                        getPickupLocation(row), //
                        getDropoffLocation(row), //
                        getDistance(row), //
                        submissionTimeDate, //
                        pickupTime, //
                        dropoffTime);
                list.add(trip);
            } catch (Exception exception) {
                exception.printStackTrace();
                unreadable.add(row.toString());
            }
        });
        return list;
    }

    public int getNumberOfTaxis() {
        return taxiIds.size();
    }

    public void saveUnreadable(File file) {
        System.err.println("Number of unreadable rows:  " + unreadable.size());
        System.err.println("Saving unreadable lines to: " + file.getAbsolutePath());
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            unreadable.forEach(s -> {
                try {
                    bufferedWriter.write(s + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public abstract String getTripId(Row row);

    public abstract String getTaxiId(Row row);

    public abstract LocalDateTime getSubmissionTime(Row row) throws ParseException;

    public abstract LocalDateTime getPickupTime(Row row) throws ParseException;

    public abstract LocalDateTime getDropoffTime(Row row) throws ParseException;

    public abstract Tensor getPickupLocation(Row row);

    public abstract Tensor getDropoffLocation(Row row);

    public abstract Scalar getDuration(Row row);

    public abstract Scalar getDistance(Row row);

    public abstract Scalar getWaitingTime(Row row);
}
