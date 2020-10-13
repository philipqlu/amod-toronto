package amodeus.amodtaxi.scenario.toronto;

import java.text.ParseException;
import java.time.LocalDateTime;

import amodeus.amodeus.util.LocalDateTimes;
import amodeus.amodeus.util.io.CsvReader.Row;
import amodeus.amodeus.util.math.SI;
import amodeus.amodtaxi.scenario.TaxiTripsReader;
import amodeus.amodtaxi.scenario.chicago.ScenarioConstants;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public class TripsReaderToronto extends TaxiTripsReader {

    public TripsReaderToronto() {
        super(",");
    }

    @Override
    public String getTripId(Row row) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public final String getTaxiId(Row line) {
        return null;
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
        return Tensors.vector(Double.valueOf(line.get("origin_long")), //
                Double.valueOf(line.get("origin_lat")));
    }

    @Override
    public Tensor getDropoffLocation(Row line) {
        return Tensors.vector(Double.valueOf(line.get("dest_long")), //
                Double.valueOf(line.get("dest_lat")));
    }

    @Override
    public Scalar getDuration(Row line) {
    	double seconds = Double.valueOf(line.get("duration")) * 60;
        return Quantity.of((long) seconds, SI.SECOND);
    }

    @Override
    public final Scalar getDistance(Row line) {
        return Quantity.of(
        		Double.valueOf(line.get("distance")) * ScenarioConstants.kmToM,
        		SI.METER);
    }

    @Override
    public final Scalar getWaitingTime(Row line) {
        // not available from data
        return null;
    }

    @Override
    public LocalDateTime getSubmissionTime(Row row) throws ParseException {
        // not available from data
        return null;
    }
}
