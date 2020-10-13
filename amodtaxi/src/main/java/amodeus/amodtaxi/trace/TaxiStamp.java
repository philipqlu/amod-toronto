// code by jph
package amodeus.amodtaxi.trace;

import java.time.LocalDateTime;

import amodeus.amodeus.dispatcher.core.RequestStatus;
import amodeus.amodeus.dispatcher.core.RoboTaxiStatus;
import org.matsim.api.core.v01.Coord;

public class TaxiStamp {

    public LocalDateTime globalTime; /* unix epoch time in [s] */
    public RoboTaxiStatus roboTaxiStatus;
    public int requestIndex;
    public Coord gps; /* in format [longitude, latitude] */
    public int linkIndex;
    public double linkSpeed;
    public boolean occupied = false;

    // TODO remove this after integrating Zurich code
    @Deprecated
    public RequestStatus requestStatus;
}
