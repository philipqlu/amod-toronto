/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.linkspeed.iterative;

import amodeus.amodeus.linkspeed.LSDataTravelTime;
import amodeus.amodeus.linkspeed.LinkSpeedDataContainer;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.router.DijkstraFactory;
import org.matsim.core.router.costcalculators.OnlyTimeDependentTravelDisutility;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelTime;

public enum LinkSpeedLeastPathCalculator {
    ;

    public static LeastCostPathCalculator from(Network network, LinkSpeedDataContainer lsData) {
        TravelTime travelTime = new LSDataTravelTime(lsData);
        return new DijkstraFactory().createPathCalculator(network, //
                new OnlyTimeDependentTravelDisutility(travelTime), travelTime);
    }
}
