/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.chicago;

import amodeus.amodeus.data.LocationSpec;
import amodeus.amodeus.data.LocationSpecDatabase;

public enum ChicagoGeoInformation {
    ;

    public static void setup() {
        for (LocationSpec locationSpec : ChicagoLocationSpecs.values())
            LocationSpecDatabase.INSTANCE.put(locationSpec);
    }
}
