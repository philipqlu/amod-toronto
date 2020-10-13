/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.data;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

import amodeus.amodeus.util.Duration;
import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodtaxi.trace.TaxiStamp;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ enum JourneyTimes {
    ;

    /** @return a {@link Tensor} containing all the journey times visible in the
     *         data @param sortedEntries, whereas the journey time is the time period
     *         during which the taxi was labeled with status occupied
     * 
     * @throws Exception */
    public static Tensor in(Collection<TaxiStamp> taxiStamps) throws Exception {
        Tensor journeyTimes = Tensors.empty();
        if (taxiStamps.stream().noneMatch(taxiStamp -> taxiStamp.occupied))
            return journeyTimes;

        LinkedList<TaxiStamp> taxiStampsSorted = taxiStamps.stream().sorted(Comparator.comparing(taxiStamp -> taxiStamp.globalTime)) //
                .collect(Collectors.toCollection(LinkedList::new));

        LocalDateTime journeyStart = null;
        boolean occPrev = false;
        LocalDateTime timePrev = taxiStampsSorted.peekFirst().globalTime;
        for (TaxiStamp taxiStamp : taxiStampsSorted) {
            LocalDateTime time = taxiStamp.globalTime;
            boolean occ = taxiStamp.occupied;
            if (occ && !occPrev) // journey has started
                journeyStart = timePrev;
            if (!occ && occPrev) { // journey has ended
                GlobalAssert.that(Objects.nonNull(journeyStart));
                GlobalAssert.that(!occ);
                GlobalAssert.that(occPrev);
                Scalar journeyTime = !timePrev.equals(journeyStart) ? //
                        Duration.between(journeyStart, timePrev) : //
                        Duration.between(journeyStart, time);
                journeyTimes.append(Sign.requirePositive(journeyTime));
                journeyStart = null;
            }
            if (occ && time == taxiStampsSorted.peekLast().globalTime) { // recordings end
                GlobalAssert.that(Objects.nonNull(journeyStart));
                Scalar journeyTime = Duration.between(journeyStart, time);
                journeyTimes.append(Sign.requirePositive(journeyTime));
                journeyStart = null;
            }
            occPrev = occ;
            timePrev = time;
        }

        return journeyTimes;
    }
}
