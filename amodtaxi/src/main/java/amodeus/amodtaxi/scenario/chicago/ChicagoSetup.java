/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.chicago;

import java.io.File;

import amodeus.amodeus.util.io.Locate;
import amodeus.amodtaxi.scenario.ScenarioSetup;

public enum ChicagoSetup {
    ;

    public static void in(File workingDir) throws Exception {
        ChicagoGeoInformation.setup();
        try {
            File resourcesDir = new File(Locate.repoFolder(ChicagoScenarioCreation.class, "amodtaxi"), "src/main/resources/chicagoScenario");
            ScenarioSetup.in(workingDir, resourcesDir);
        } catch (Exception e) {
            ScenarioSetup.in(workingDir, "chicagoScenario");
        }
    }
}
