/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.population;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import amodeus.amodeus.net.FastLinkLookup;
import amodeus.amodeus.taxitrip.TaxiTrip;
import ch.ethz.idsc.tensor.red.Total;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.dvrp.router.DistanceAsTravelDisutility;
import org.matsim.core.router.FastAStarLandmarksFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.trafficmonitoring.FreeSpeedTravelTime;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ class TaxiDistanceCalculator {

    private final Map<String, NavigableSet<TaxiTrip>> tripMap = new HashMap<>();
    private final LeastCostPathCalculator lcpc;
    private final FastLinkLookup fastLinkLookup;
    private final File exportFolder;

    public TaxiDistanceCalculator(File exportFolder, Network network, FastLinkLookup fastLinkLookup) {
        this.exportFolder = exportFolder;
        this.lcpc = new FastAStarLandmarksFactory(Runtime.getRuntime().availableProcessors()).createPathCalculator( //
                network, new DistanceAsTravelDisutility(), new FreeSpeedTravelTime());
        this.fastLinkLookup = fastLinkLookup;
    }

    public void addTrip(TaxiTrip taxiTrip) {
        tripMap.computeIfAbsent(taxiTrip.taxiId, id -> new TreeSet<>()) // taxi trips are sorted according to pickup date
                /* tripMap.get(taxiTrip.taxiId) */ .add(taxiTrip);
    }

    public void exportTotalDistance() throws IOException {
        Map<String, Tensor> totalDistances = new HashMap<>();
        tripMap.forEach((taxiId, trips) -> {
            // initialize
            Scalar taxiTotDist = Quantity.of(0, "km");
            Scalar taxiEmptyDistance = Quantity.of(0, "km");

            // calculate minimum trip and intra trip distances
            TaxiTrip tripBefore = null;
            for (TaxiTrip trip : trips) {
                // distance of current trip
                Link tStart = fastLinkLookup.linkFromWGS84(trip.pickupLoc);
                Link tDesti = fastLinkLookup.linkFromWGS84(trip.dropoffLoc);
                Scalar tripDist = shortPathDistance(tStart, tDesti);
                taxiTotDist = taxiTotDist.add(tripDist);
                if (Objects.nonNull(tripBefore)) {
                    // intra trip distance
                    Link tDestidBefore = fastLinkLookup.linkFromWGS84(tripBefore.dropoffLoc);
                    Scalar intraTripDist = shortPathDistance(tDestidBefore, tStart);
                    taxiTotDist = taxiTotDist.add(intraTripDist);
                    taxiEmptyDistance = taxiEmptyDistance.add(intraTripDist);
                }
                tripBefore = trip;
            }
            totalDistances.put(taxiId, Tensors.of(taxiTotDist, taxiEmptyDistance));
        });

        // export
        Tensor export = Tensor.of(totalDistances.entrySet().stream().map(e -> Tensors.of(Scalars.fromString(e.getKey()), e.getValue())));
        Scalar fleetDistance = Total.ofVector(export.get(Tensor.ALL, 1, 0));
        Scalar fleetDistanceEmpty = Total.ofVector(export.get(Tensor.ALL, 1, 1));

        export.append(Tensors.of(Scalars.fromString("{fleet}"), Tensors.of(fleetDistance, fleetDistanceEmpty)));
        Export.of(new File(exportFolder, "minFleetDistance.csv"), export);
        int totalTrips = tripMap.values().stream().mapToInt(Set::size).sum();
        System.out.println("Total trips: " + totalTrips);
    }

    private Scalar shortPathDistance(Link linkStart, Link to) {
        Path shortest = lcpc.calcLeastCostPath(linkStart.getFromNode(), to.getToNode(), 1, null, null);
        double distance = shortest.links.stream().mapToDouble(Link::getLength).sum();
        return Quantity.of(distance / 1000.0, "km");
    }
}
