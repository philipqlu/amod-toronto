/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.data;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

import amodeus.amodeus.net.FastLinkLookup;
import amodeus.amodeus.util.math.SI;
import amodeus.amodtaxi.trace.TaxiStamp;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;

/* package */ class NetworkDistanceHelper {
    private Scalar custrDistance = Quantity.of(0, SI.METER);
    private Scalar emptyDistance = Quantity.of(0, SI.METER);

    public NetworkDistanceHelper(Collection<TaxiStamp> taxiStamps, FastLinkLookup fastLinkLookup, LeastCostPathCalculator leastCostPathCalculator) {
        /** compute */
        LinkedList<TaxiStamp> taxiStampsSorted = taxiStamps.stream().sorted(Comparator.comparing(taxiStamp -> taxiStamp.globalTime)) //
                .collect(Collectors.toCollection(LinkedList::new));
        Link linkStart = fastLinkLookup.linkFromWGS84(taxiStampsSorted.peekFirst().gps);
        Link linkPrev = linkStart;
        boolean occPrev = taxiStampsSorted.peekFirst().occupied;

        for (TaxiStamp taxiStamp : taxiStampsSorted) {
            boolean occ = taxiStamp.occupied;

            // 1: lat 0: lng
            Link currLink = fastLinkLookup.linkFromWGS84(taxiStamp.gps);
            if (occ != occPrev) {
                Scalar distance = distance(linkStart, linkPrev, leastCostPathCalculator);
                if (occ) // request started
                    emptyDistance = emptyDistance.add(distance);
                else // request ended
                    custrDistance = custrDistance.add(distance);
                linkStart = currLink;
            }
            linkPrev = currLink;
            occPrev = occ;
        }
    }

    private static Scalar distance(Link fromLink, Link toLink, LeastCostPathCalculator leastCostPathCalculator) {
        Path shortest = leastCostPathCalculator.calcLeastCostPath(fromLink.getFromNode(), toLink.getToNode(), 1, null, null);
        return Quantity.of(shortest.links.stream().mapToDouble(Link::getLength).sum(), SI.METER);
    }

    public Scalar getEmptyDistance() {
        return emptyDistance;
    }

    public Scalar getCustomerDistance() {
        return custrDistance;
    }

    public Scalar getTotalDistance() {
        return emptyDistance.add(custrDistance);
    }
}
