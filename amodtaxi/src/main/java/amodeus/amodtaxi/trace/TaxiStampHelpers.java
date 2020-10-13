package amodeus.amodtaxi.trace;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import org.matsim.api.core.v01.Coord;

public enum TaxiStampHelpers {
    ;

    /** @param taxiStamps {@link Collection} of {@link TaxiStamp}
     * @return {minLat, maxLat, minLong, maxLong} */
    public static Optional<Tensor> boundaries(Collection<TaxiStamp> taxiStamps) {
        OptionalDouble minLng = minLng(taxiStamps);
        return minLng.isPresent() //
                ? Optional.of(Tensors.vectorDouble(minLat(taxiStamps).getAsDouble(), //
                    maxLat(taxiStamps).getAsDouble(), //
                    minLng.getAsDouble(), //
                    maxLng(taxiStamps).getAsDouble())) //
                : Optional.empty();
    }

    public static OptionalDouble minLng(Collection<TaxiStamp> taxiStamps) {
        return taxiStamps.stream().map(taxiStamp -> taxiStamp.gps).mapToDouble(Coord::getX).min();
    }

    public static OptionalDouble maxLng(Collection<TaxiStamp> taxiStamps) {
        return taxiStamps.stream().map(taxiStamp -> taxiStamp.gps).mapToDouble(Coord::getX).max();
    }

    public static OptionalDouble minLat(Collection<TaxiStamp> taxiStamps) {
        return taxiStamps.stream().map(taxiStamp -> taxiStamp.gps).mapToDouble(Coord::getY).min();
    }

    public static OptionalDouble maxLat(Collection<TaxiStamp> taxiStamps) {
        return taxiStamps.stream().map(taxiStamp -> taxiStamp.gps).mapToDouble(Coord::getY).max();
    }

    public static Optional<LocalDateTime> minTime(Collection<TaxiStamp> taxiStamps) {
        return taxiStamps.stream().map(taxiStamp -> taxiStamp.globalTime).min(LocalDateTime::compareTo);
    }

    public static Optional<LocalDateTime> maxTime(Collection<TaxiStamp> taxiStamps) {
        return taxiStamps.stream().map(taxiStamp -> taxiStamp.globalTime).max(LocalDateTime::compareTo);
    }

    public static Optional<LocalDateTime> minPickupTime(Collection<TaxiStamp> taxiStamps) {
        return pickupStream(taxiStamps).map(taxiStamp -> taxiStamp.globalTime).min(LocalDateTime::compareTo);
    }

    public static Optional<LocalDateTime> maxPickupTime(Collection<TaxiStamp> taxiStamps) {
        return pickupStream(taxiStamps).map(taxiStamp -> taxiStamp.globalTime).max(LocalDateTime::compareTo);
    }

    public static long numberOfRequests(Collection<TaxiStamp> taxiStamps) {
        return pickupStream(taxiStamps).count();
    }

    public static Collection<LocalDateTime> pickupTimes(Collection<TaxiStamp> taxiStamps) {
        return pickupStream(taxiStamps).map(taxiStamp -> taxiStamp.globalTime).collect(Collectors.toList());
    }

    private static Stream<TaxiStamp> pickupStream(Collection<TaxiStamp> taxiStamps) {
        AtomicBoolean occPrev = new AtomicBoolean(false);
        return sortedStream(taxiStamps).filter(taxiStamp -> {
            boolean occ = taxiStamp.occupied;
            return !occPrev.getAndSet(occ) && occ;
        });
    }

    private static Stream<TaxiStamp> sortedStream(Collection<TaxiStamp> taxiStamps) {
        return taxiStamps.stream().sorted(Comparator.comparing(taxiStamp -> taxiStamp.globalTime));
    }
}
