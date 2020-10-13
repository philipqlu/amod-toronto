/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.sanfrancisco;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import amodeus.amodeus.dispatcher.core.RequestStatus;
import amodeus.amodeus.dispatcher.core.RoboTaxiStatus;
import amodeus.amodeus.net.FastLinkLookup;
import amodeus.amodeus.net.RequestContainer;
import amodeus.amodeus.net.SimulationObject;
import amodeus.amodeus.net.VehicleContainer;
import amodeus.amodeus.util.AmodeusTimeConvert;
import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodtaxi.trace.TaxiStamp;
import ch.ethz.idsc.tensor.io.Serialization;

/* package */ class SimContainerPopulator {
    private final FastLinkLookup fastLinkLookup;
    private final AmodeusTimeConvert timeConvert;

    public SimContainerPopulator(FastLinkLookup fastLinkLookup, AmodeusTimeConvert timeConvert) {
        this.fastLinkLookup = fastLinkLookup;
        this.timeConvert = timeConvert;
    }

    public void with(TaxiStamp taxiStamp, int vehicleIndex, //
            SimulationObject simulationObject, //
            GlobalRequestIndex globalReqIndex, //
            LocalDate simulationDate, RequestInserter reqInserter) {
        Objects.requireNonNull(taxiStamp);
        Objects.requireNonNull(reqInserter);

        int linkIndex = fastLinkLookup.indexFromWGS84(taxiStamp.gps);

        /** initialize and add VehicleContainer */
        VehicleContainer vc = new VehicleContainer();
        vc.vehicleIndex = vehicleIndex;
        vc.linkTrace = new int[] { linkIndex };
        vc.statii = new RoboTaxiStatus[] { Objects.requireNonNull(taxiStamp.roboTaxiStatus) };
        vc.destinationLinkIndex = linkIndex; // TODO this is just temporary, need to do properly
        simulationObject.vehicles.add(vc);

        /** request containers */
        for (RequestContainer rc : reqInserter.getReqContainerCopy(taxiStamp)) {
            /** ensure that globally every index occurs only once */
            LocalDateTime firstValidSubmissionTime = timeConvert.beginOf(simulationDate);
            if (timeConvert.ldtToAmodeus(firstValidSubmissionTime, simulationDate) <= rc.submissionTime) {
                Integer globalIndex = globalReqIndex.add(vehicleIndex, rc.requestIndex);

                RequestContainer copy;
                try {
                    copy = Serialization.copy(rc);
                } catch (Exception e) {
                    throw new RuntimeException();
                }
                // System.out.println("copy submission time: " + copy.submissionTime);
                // copy.submissionTime = timeConvert.toAmodeus((int) copy.submissionTime, localDate);
                copy.requestIndex = globalIndex;
                GlobalAssert.that(copy.submissionTime >= 0);
                simulationObject.requests.add(copy);
                if (copy.requestStatus.contains(RequestStatus.PICKUP))
                    simulationObject.total_matchedRequests += 1;
            }
        }
    }

    public void withEmpty(SimulationObject simulationObject, int vehicleIndex) {
        /** place vehicles on arbitrary link */
        VehicleContainer vc = new VehicleContainer();
        vc.vehicleIndex = vehicleIndex;
        vc.linkTrace = new int[] { 1 };
        vc.statii = new RoboTaxiStatus[] { RoboTaxiStatus.STAY };
        simulationObject.vehicles.add(vc);
    }
}
