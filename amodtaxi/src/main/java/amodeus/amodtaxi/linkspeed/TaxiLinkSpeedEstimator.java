/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.linkspeed;

import amodeus.amodeus.linkspeed.LinkSpeedDataContainer;

public interface TaxiLinkSpeedEstimator {

    /** @return the {@link LinkSpeedDataContainer} produced by the {@link TaxiLinkSpeedEstimator} */
    LinkSpeedDataContainer getLsData();
}
