/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.linkspeed.batch;

import java.util.Collection;

import amodeus.amodeus.util.geo.FastQuadTree;
import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodeus.util.math.SI;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.utils.collections.QuadTree;

import amodeus.amodtaxi.linkspeed.NeighborKernel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;

public class ProximityNeighborKernel implements NeighborKernel {
    private final QuadTree<Link> quadTree;
    private final Scalar radius;

    /** To work the {@link ProximityNeighborKernel} requires the {@link Network} @param network it
     * runs on as well as the @param radius in which links are classified as neighbors
     * where the @param radius is a {@link Quantity} in [m] */
    public ProximityNeighborKernel(Network network, Scalar radius) {
        this.radius = radius;
        GlobalAssert.that(Scalars.lessThan(Quantity.of(0, SI.METER), radius));
        this.quadTree = FastQuadTree.of(network);
    }

    @Override // from NeighborKernel
    public Collection<Link> getNeighbors(Link link) {
        double x1 = link.getCoord().getX();
        double y1 = link.getCoord().getY();
        return quadTree.getDisk(x1, y1, radius.number().doubleValue());
    }
}
