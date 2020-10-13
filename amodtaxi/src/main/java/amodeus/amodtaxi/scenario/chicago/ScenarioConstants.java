/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.chicago;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

import amodeus.amodeus.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public enum ScenarioConstants {
    ;

	public static final double kmToM = 1000.0;
    public static final double milesToM = 1609.34;
    public static final DateFormat inFormat = new SimpleDateFormat("yyyy/MM/dd");
    public static final DateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static final DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    public static final DateTimeFormatter onlineFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static final DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
    public static final Scalar maxAllowedSpeed = Quantity.of(37.9984, "m*s^-1");
    public static final Scalar maxEndTime = Quantity.of(107999.9, SI.SECOND);
}
