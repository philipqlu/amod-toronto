/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.tripmodif;

import junit.framework.TestCase;

import ch.ethz.idsc.tensor.io.StringScalarQ;

public class ChicagoAirportBoundaryLinksTest extends TestCase {
    public void testSimple() {
        assertFalse(StringScalarQ.any(ChicagoAirportBoundaryLinks.locations()));
    }
}
