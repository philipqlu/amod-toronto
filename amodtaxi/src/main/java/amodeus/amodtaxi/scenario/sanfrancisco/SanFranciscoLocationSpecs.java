/* amod - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.sanfrancisco;

import amodeus.amodeus.data.LocationSpec;
import amodeus.amodeus.data.ReferenceFrame;
import org.matsim.api.core.v01.Coord;

/* package */ enum SanFranciscoLocationSpecs implements LocationSpec {
    SANFRANCISCO( //
            SanFranciscoReferenceFrames.SANFRANCISCO, //
            new Coord(-122.4363005, 37.7511686)), // <- no cutting
    ;

    private final ReferenceFrame referenceFrame;
    /** increasing the first value goes east
     * increasing the second value goes north */
    private final Coord center;

    SanFranciscoLocationSpecs(ReferenceFrame referenceFrame, Coord center) {
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
