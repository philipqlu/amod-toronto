/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.tripmodif;

import java.util.Set;
import java.util.stream.Collectors;

import amodeus.amodeus.net.FastLinkLookup;
import amodeus.amodeus.net.TensorCoords;
import org.matsim.api.core.v01.network.Link;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** These locations are added as additional centroids to the virtual network
 * of the Chicago scenario. This ensures that taxi demand at the airport
 * stays at the airport itself and is not distributed to neibhoring zones
 * in the process of the scenario creation. */
/* package */ enum ChicagoAirportBoundaryLinks {
    ;

    private static final Tensor LOCATIONS = Tensors.fromString("{" //
            // O'Hare International Airport
            + "{-87.9077 ,42.0089 }," //
            + "{-87.89924,41.9793 }," //
            + "{-87.9125 ,41.9635 }," //
            + "{-87.9381 ,41.9822 }," //
            // Midway International Airport
            + "{-87.75324,41.79337}," //
            + "{-87.7415 ,41.7857 }," //
            + "{-87.7524 ,41.7783 }," //
            + "{-87.74704,41.79306}," //
            + "{-87.762  ,41.7872 }" //
            + "}").unmodifiable();

    public static Set<String> get(FastLinkLookup fastLinkLookup) {
        return LOCATIONS.stream() //
                .map(TensorCoords::toCoord) //
                .map(fastLinkLookup::linkFromWGS84) //
                .map(Link::getId) //
                .map(Object::toString) //
                .collect(Collectors.toSet());
    }

    /* package */ static Tensor locations() {
        return LOCATIONS;
    }
}
