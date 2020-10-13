/* amod - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.sanfrancisco;

import amodeus.amodeus.data.ReferenceFrame;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.GeotoolsTransformation;

public enum SanFranciscoReferenceFrames implements ReferenceFrame {
    SANFRANCISCO( //
            new GeotoolsTransformation("EPSG:26743", "WGS84"), //
            new GeotoolsTransformation("WGS84", "EPSG:26743")), //
    ;

    // ---

    private final CoordinateTransformation coords_toWGS84;
    private final CoordinateTransformation coords_fromWGS84;

    SanFranciscoReferenceFrames(CoordinateTransformation c1, CoordinateTransformation c2) {
        coords_toWGS84 = c1;
        coords_fromWGS84 = c2;
    }

    @Override
    public CoordinateTransformation coords_fromWGS84() {
        return coords_fromWGS84;
    }

    @Override
    public CoordinateTransformation coords_toWGS84() {
        return coords_toWGS84;
    }
}
