/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.linkspeed.batch;

import java.util.Collection;
import java.util.SortedMap;

import amodeus.amodeus.linkspeed.LinkSpeedDataContainer;
import amodeus.amodeus.linkspeed.LinkSpeedTimeSeries;
import amodeus.amodeus.util.math.GlobalAssert;
import org.matsim.api.core.v01.network.Link;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Mean;

/* package */ enum MeanLinkSpeed {
    ;

    /** @return mean speed for a {@link Link} @param link with neighboring {@link Link}s @param neighbors
     *         at a certain @param time. The rationale of the approach is that the reduction / increase of the link
     *         speed is the sage as the average of its neighbors. */
    public static Scalar ofNeighbors(Collection<Link> neighbors, Integer time, Link link, LinkSpeedDataContainer lsData) {
        Tensor changes = Tensors.empty();
        for (Link neighbor : neighbors) {

            /** retrieve the link speed estimate of the neighbor */
            SortedMap<Integer, LinkSpeedTimeSeries> neighborMap = lsData.getLinkMap();
            LinkSpeedTimeSeries series = neighborMap.get(Integer.parseInt(neighbor.getId().toString()));
            GlobalAssert.that(time >= 0);
            try {
                // Tensor speeds = series.getSpeedsAt(time);
                Scalar mean = RealScalar.of(series.getSpeedsAt(time));// (Scalar) Mean.of(speeds);

                Scalar freeFlow = RealScalar.of(neighbor.getFreespeed());
                Scalar change = mean.divide(freeFlow);

                // System.out.println("speeds: " + speeds);
                // System.out.println("mean: " + mean);
                // System.out.println("freeFlow: " + freeFlow);
                // System.out.println("change: " + change);
                // Thread.sleep(10);

                changes.append(change);
            } catch (Exception exception) {
                // TODO this catch is necessary because the class LinkSpeedTimeSeries
                // is not able to say if any recordings were made without creating an exception,
                // fix eventually...
                // --
            }
        }

        if (changes.length() == 0)
            return null;

        // System.out.println("changes: " + changes);
        Scalar meanReduction = (Scalar) Mean.of(changes);
        // if (Scalars.lessThan(RealScalar.ONE, meanReduction))
        // System.out.println("meanReduction: " + meanReduction);
        // System.out.println("===");
        return RealScalar.of(link.getFreespeed()).multiply(meanReduction);
    }
}
