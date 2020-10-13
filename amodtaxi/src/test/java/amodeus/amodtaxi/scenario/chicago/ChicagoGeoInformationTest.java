/* amodtaxi - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.chicago;

import amodeus.amodeus.data.LocationSpec;
import amodeus.amodeus.data.LocationSpecDatabase;
import junit.framework.TestCase;

public class ChicagoGeoInformationTest extends TestCase {
    public void testSimple() throws Exception {
        /* Function to check */
        ChicagoGeoInformation.setup();

        /* Check if all LocationSpecs were loaded */
        for (LocationSpec locationSpec : ChicagoLocationSpecs.values())
            assertEquals(LocationSpecDatabase.INSTANCE.fromString(locationSpec.name()), locationSpec);
    }
}
