/* amod - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.chicago;

import amodeus.amodeus.data.LocationSpec;
import amodeus.amodeus.data.ReferenceFrame;
import org.matsim.api.core.v01.Coord;

/* package */ enum ChicagoLocationSpecs implements LocationSpec {
    CHICAGO( //
            ChicagoReferenceFrames.CHICAGO, //
            new Coord(-74.005, 40.712))// , // Updated), // <- no cutting
    ;

    private final ReferenceFrame referenceFrame;
    /** increasing the first value goes east
     * increasing the second value goes north */
    private final Coord center;

    private ChicagoLocationSpecs(ReferenceFrame referenceFrame, Coord center) {
        this.referenceFrame = referenceFrame;
        this.center = center;
    }

    @Override
    public ReferenceFrame referenceFrame() {
        return referenceFrame;
    }

    @Override
    public Coord center() {
        return center;
    }
}
