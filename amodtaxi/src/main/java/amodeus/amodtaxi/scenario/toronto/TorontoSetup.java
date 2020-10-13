package amodeus.amodtaxi.scenario.toronto;

import java.io.File;

import amodeus.amodeus.util.io.Locate;
import amodeus.amodtaxi.scenario.ScenarioLabels;
import amodeus.amodtaxi.scenario.ScenarioSetup;

public enum TorontoSetup {
	;
	
	public static void in(File workingDir) throws Exception {
		TorontoGeoInformation.setup();
		try {
			File resourcesDir = new File(Locate.repoFolder(TorontoScenarioCreation.class, "amodtaxi"), "src/main/resources/torontoScenario");
			ScenarioSetup.in(workingDir, resourcesDir);
		} catch (Exception e) {
			ScenarioSetup.in(workingDir, "torontoScenario");
		}	
	}
}
