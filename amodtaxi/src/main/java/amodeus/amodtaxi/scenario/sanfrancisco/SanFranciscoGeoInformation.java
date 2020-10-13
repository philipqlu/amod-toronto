/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.sanfrancisco;

import amodeus.amodeus.data.LocationSpec;
import amodeus.amodeus.data.LocationSpecDatabase;

public class SanFranciscoGeoInformation {

    public static void setup() {
        for (LocationSpec locationSpec : SanFranciscoLocationSpecs.values())
            LocationSpecDatabase.INSTANCE.put(locationSpec);
    }
}
