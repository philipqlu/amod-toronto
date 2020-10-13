package amodeus.amodtaxi.scenario.chicago;

import java.io.File;

import amodeus.amodeus.util.io.GZHandler;
import amodeus.amodtaxi.scenario.ScenarioLabels;
import amodeus.amodtaxi.scenario.ScenarioSetup;
import amodeus.amodtaxi.scenario.TestDirectories;

/* package */ enum ChicagoTestSetup {
    ;

    public static void in(File workingDir) throws Exception {
        ChicagoGeoInformation.setup();
        ScenarioSetup.in(workingDir, TestDirectories.CHICAGO, ScenarioLabels.networkGz);
        GZHandler.extract(new File(workingDir, ScenarioLabels.networkGz), new File(workingDir, ScenarioLabels.network));
    }
}
