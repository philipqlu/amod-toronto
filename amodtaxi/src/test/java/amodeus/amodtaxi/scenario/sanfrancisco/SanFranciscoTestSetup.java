package amodeus.amodtaxi.scenario.sanfrancisco;

import java.io.File;

import amodeus.amodeus.util.io.GZHandler;
import amodeus.amodtaxi.scenario.ScenarioLabels;
import amodeus.amodtaxi.scenario.ScenarioSetup;
import amodeus.amodtaxi.scenario.TestDirectories;

/* package */ enum SanFranciscoTestSetup {
    ;

    public static void in(File workingDir) throws Exception {
        SanFranciscoGeoInformation.setup();
        ScenarioSetup.in(workingDir, TestDirectories.SAN_FRANCISCO, ScenarioLabels.networkGz);
        GZHandler.extract(new File(workingDir, ScenarioLabels.networkGz), new File(workingDir, ScenarioLabels.network));
    }
}
