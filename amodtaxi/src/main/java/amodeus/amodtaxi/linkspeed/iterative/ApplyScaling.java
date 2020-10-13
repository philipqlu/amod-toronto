/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.linkspeed.iterative;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import amodeus.amodeus.linkspeed.LinkSpeedDataContainer;
import amodeus.amodeus.linkspeed.LinkSpeedTimeSeries;
import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodeus.util.math.GlobalAssert;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/* package */ enum ApplyScaling {
    ;

    private static boolean allowIncrease = false;

    public static void to(LinkSpeedDataContainer lsData, TaxiTrip trip, Path path, Scalar rescalefactor, int dt) {
        int tripStart = StaticHelper.startTime(trip);
        int tripEnd = StaticHelper.endTime(trip);

        for (Link link : path.links) {
            /** get link properties */
            double freeSpeed = link.getFreespeed();
            LinkSpeedTimeSeries lsTime = lsData.get(link);

            /** if no recordings are present, initialize with free speed for duration of trip */
            if (Objects.isNull(lsTime)) {
                // for (int time = tripStart; time <= tripEnd; time += dt) {
                // lsData.addData(link, time, freeSpeed);
                // }
                // TODO remove magic const. really necessary all day?
                for (int time = 0; time <= 108000; time += dt)
                    lsData.addData(link, time, freeSpeed);
            }
            lsTime = lsData.get(link);
            Objects.requireNonNull(lsTime);

            List<Integer> relevantTimes = lsTime.getRecordedTimes().stream() //
                    .filter(time -> tripStart <= time && time <= tripEnd).collect(Collectors.toList());

            if (relevantTimes.size() == 0) // must have at least one entry for convergence
                relevantTimes.add(lsTime.getTimeFloor(tripStart));

            GlobalAssert.that(relevantTimes.size() > 0);

            for (int time : lsTime.getRecordedTimes()) {
                Scalar speedNow = RealScalar.of(freeSpeed);
                Double recorded = lsTime.getSpeedsAt(time);
                if (Objects.nonNull(recorded))
                    speedNow = RealScalar.of(recorded);
                Scalar newSpeedS = speedNow.multiply(rescalefactor);
                double newSpeed = newSpeedS.number().doubleValue();

                // NOW
                if (newSpeed <= link.getFreespeed() || allowIncrease)
                    lsTime.setSpeed(time, newSpeed);
            }
        }
    }
}
