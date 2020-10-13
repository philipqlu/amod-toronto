/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.linkspeed.batch;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import amodeus.amodeus.linkspeed.LinkSpeedDataContainer;
import amodeus.amodeus.net.MatsimAmodeusDatabase;
import amodeus.amodeus.taxitrip.ShortestDurationCalculator;
import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodeus.taxitrip.TaxiTripCheck;
import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodeus.util.math.SI;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;

import amodeus.amodtaxi.linkspeed.TaxiLinkSpeedEstimator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.qty.Quantity;

public class FlowTimeInvLinkSpeed implements TaxiLinkSpeedEstimator {
    private final boolean verbose = false;
    private final LinkSpeedDataContainer lsData;
    // TODO hourDt?
    public final static Scalar dayDt = Quantity.of(3600, SI.SECOND);

    public FlowTimeInvLinkSpeed(Collection<TaxiTrip> records, Network network, MatsimAmodeusDatabase db, TrafficDelayEstimate delayCalculator) {

        /** ensure {@link TaxiTrip}s contain all required information */
        GlobalAssert.that(records.stream().filter(TaxiTripCheck::isOfMinimalScope).count() == records.size());

        /** compute a path for every record, scale path such that end time
         * is as in the {@link TaxiTrip}, then break into parts as time
         * steps of recording */
        ShortestDurationCalculator calc = new ShortestDurationCalculator(network, db);
        Collection<PathHandlerTimeInv> paths = records.stream().map(tt -> new PathHandlerTimeInv(tt, calc)).collect(Collectors.toList());

        System.out.println("Totally found " + paths.size() + " paths for flow link speed computation.");

        /** remove paths which trip duration > free flow duration */
        paths.removeIf(p -> !p.isValid());

        /** record traveled links */
        Set<Link> travelledLinks = paths.stream().flatMap(p -> p.travelledLinks.stream()).collect(Collectors.toSet());

        HashMap<Integer, Link> localIndexLink = new HashMap<>();
        HashMap<Link, Integer> localLinkIndex = new HashMap<>();
        int j = 0;
        for (Link link : travelledLinks) {
            localIndexLink.put(j, link);
            localLinkIndex.put(link, j);
            ++j;
        }
        GlobalAssert.that(travelledLinks.size() == localIndexLink.size());

        /** setup matrices A, b, solve pseudo-inverse */
        int numEq = records.size();
        int numVar = travelledLinks.size();

        Tensor flowMatrix = Array.zeros(numEq, numVar);
        Tensor freeflowTripDuration = Array.zeros(numEq, 1);
        Tensor trafficTripDuration = Array.zeros(numEq, 1);

        int k = 0;
        for (PathHandlerTimeInv ph : paths) {
            for (Link link : ph.travelledLinks) {
                int index = localLinkIndex.get(link);
                flowMatrix.set(RealScalar.ONE, k, index);
            }
            freeflowTripDuration.set(RealScalar.of(ph.freeflowDuation.number()), k, 0);
            trafficTripDuration.set(RealScalar.of(ph.duration.number()), k, 0);
            ++k;
        }

        /** flow based traffic estimation */
        FlowTrafficEstimation estimation = //
                FlowTrafficEstimation.of(flowMatrix, freeflowTripDuration, trafficTripDuration, delayCalculator);

        Tensor estimateTravelTimeLinkDelays = Objects.requireNonNull(estimation).trafficDelays; // trafficTravelTimeEstimates();

        int nwLinks = network.getLinks().size();
        System.out.println("Number of network links:       " + nwLinks);
        List<Integer> dims = Dimensions.of(flowMatrix);
        System.out.println("Number of covered links:       " + dims.get(1) + "(" + dims.get(1) / ((double) nwLinks) + ")");
        System.out.println("Trips used for calculation:    " + dims.get(0));

        if (verbose) { // enabling these prints will result in very substantial workload...
            System.out.println("trafficDelays:              " + Dimensions.of(estimation.trafficDelays));
            System.out.println("trafficTravelTimeEstimates: " + Dimensions.of(estimation.trafficTravelTimeEstimates()));
            System.out.println("error:                      " + Dimensions.of(estimation.getError()));
        }

        /** take solution xij and assign to LinkSpeedDataContainer (necessary to transfer to deviations from freeflow speed) */
        this.lsData = new LinkSpeedDataContainer();
        int numNegSpeed = 0;

        int numReduction = 0;
        int numIncrease = 0;
        int numSame = 0;

        for (int i = 0; i < estimateTravelTimeLinkDelays.length(); ++i) {
            Link link = localIndexLink.get(i);
            Objects.requireNonNull(link);
            GlobalAssert.that(network.getLinks().values().contains(link));

            /** calculate the link speed */
            double length = link.getLength();
            double freeSpeed = link.getFreespeed();
            double freeTravelTime = length / freeSpeed;
            GlobalAssert.that(freeSpeed > 0);
            GlobalAssert.that(length > 0);

            double congestionDelay = estimateTravelTimeLinkDelays.Get(i, 0).number().doubleValue();
            if (congestionDelay < 0)
                System.err.println("congestionDelay: " + congestionDelay);
            GlobalAssert.that(congestionDelay >= 0);

            double trafficTravelTime = freeTravelTime + congestionDelay;
            double speed = length / trafficTravelTime;
            if (trafficTravelTime < freeTravelTime) {
                System.err.println("traffic: " + trafficTravelTime);
                System.err.println("free:    " + freeTravelTime);
            }

            if (speed == freeSpeed)
                ++numSame;
            if (speed < freeSpeed)
                ++numReduction;
            if (speed - freeSpeed > 0.1) {
                ++numIncrease;
                System.out.println("speed:             " + speed);
                System.out.println("freeSpeed:         " + freeSpeed);
                System.out.println("length:            " + length);
                System.out.println("trafficTravelTime: " + trafficTravelTime);
                System.out.println("===");
            }

            /** add for all times */
            for (int bin = 0; bin * dayDt.number().intValue() < 108000; ++bin) {
                int time = RealScalar.of(bin).multiply(dayDt).number().intValue();
                GlobalAssert.that(time >= 0);
                if (trafficTravelTime >= 0) {
                    GlobalAssert.that(speed - link.getFreespeed() <= 0.01);
                    lsData.addData(link, time, speed);
                } else
                    ++numNegSpeed;
            }
        }

        System.out.println("Number of negative speeds ommitted: " + numNegSpeed);
        System.out.println("numReduction: " + numReduction);
        System.out.println("numSame: " + numSame);
        System.out.println("numIncrease: " + numIncrease);
        // System.exit(1);

        /** Apply moving average filter to modify every link not solved in the previous step */
        ProximityNeighborKernel filterKernel = new ProximityNeighborKernel(network, Quantity.of(2000, "m"));
        new LinkSpeedDataInterpolation(network, filterKernel, lsData);
    }

    @Override
    public LinkSpeedDataContainer getLsData() {
        return lsData;
    }
}
