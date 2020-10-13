/* amod - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.toronto;

import amodeus.amodeus.data.LocationSpec;
import amodeus.amodeus.data.ReferenceFrame;
import org.matsim.api.core.v01.Coord;

/* package */ enum TorontoLocationSpecs implements LocationSpec {
    TORONTO( //
            TorontoReferenceFrames.TORONTO, //
            new Coord(-79.377257,43.718394)), //
    ;

    private final ReferenceFrame referenceFrame;
    /** increasing the first value goes east
     * increasing the second value goes north */
    private final Coord center;

    TorontoLocationSpecs(ReferenceFrame referenceFrame, Coord center) {
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
