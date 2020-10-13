/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.chicago;

import java.text.ParseException;
import java.time.LocalDateTime;

import amodeus.amodeus.util.LocalDateTimes;
import amodeus.amodeus.util.io.CsvReader.Row;
import amodeus.amodeus.util.math.SI;
import amodeus.amodtaxi.scenario.TaxiTripsReader;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public class OnlineTripsReaderChicago extends TaxiTripsReader {

    public OnlineTripsReaderChicago() {
        super(",");
    }

    @Override
    public String getTripId(Row row) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public final String getTaxiId(Row row) {
        return row.get("taxi_id");
    }

    @Override
    public LocalDateTime getPickupTime(Row line) throws ParseException {
        return LocalDateTime.parse(line.get("trip_start_timestamp"), ScenarioConstants.onlineFormatter);
    }

    @Override
    public LocalDateTime getDropoffTime(Row line) throws ParseException {
        return LocalDateTimes.addTo(getPickupTime(line), getDuration(line));
    }

    @Override
    public Tensor getPickupLocation(Row line) {
        return Tensors.vector(Double.valueOf(line.get("pickup_centroid_longitude")), //
                Double.valueOf(line.get("pickup_centroid_latitude")));
    }

    @Override
    public Tensor getDropoffLocation(Row line) {
        return Tensors.vector(Double.valueOf(line.get("dropoff_centroid_longitude")), //
                Double.valueOf(line.get("dropoff_centroid_latitude")));
    }

    @Override
    public Scalar getDuration(Row line) {
        return Quantity.of(Long.valueOf(line.get("trip_seconds")), SI.SECOND);
    }

    @Override
    public final Scalar getDistance(Row row) {
        return Quantity.of(Double.valueOf(row.get("trip_miles"))//
                * ScenarioConstants.milesToM, SI.METER);
    }

    @Override
    public final Scalar getWaitingTime(Row row) {
        // not available from data
        return null;
    }

    @Override
    public LocalDateTime getSubmissionTime(Row row) throws ParseException {
        // not available from data
        return null;
    }
}

// TODO move to tests
// public static void main(String[] args) throws ParseException {
// String dateString = "2018-01-22T22:17:25.123";
//
// /** old Date stuff */
// DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");
// Date date = format.parse(dateString);
// System.out.println(date);
// /** LocalDateTime */
// DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
// LocalDateTime ldt = LocalDateTime.parse(dateString, dtf);// dtf.parse(dateString).;
// System.out.println(ldt);
//
// }
