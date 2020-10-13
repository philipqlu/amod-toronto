package amodeus.amodtaxi.scenario.toronto;

import amodeus.amodeus.data.LocationSpec;
import amodeus.amodeus.data.LocationSpecDatabase;

public enum TorontoGeoInformation {
	;
	
	public static void setup() {
		for (LocationSpec locationSpec : TorontoLocationSpecs.values()) {
			LocationSpecDatabase.INSTANCE.put(locationSpec);
		}
	}

}
