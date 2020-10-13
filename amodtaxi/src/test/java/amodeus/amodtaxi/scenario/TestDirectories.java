package amodeus.amodtaxi.scenario;

import java.io.File;

import amodeus.amodeus.util.io.Locate;
import amodeus.amodeus.util.io.MultiFileTools;

public enum TestDirectories {
    ;

    public static final File WORKING = new File(MultiFileTools.getDefaultWorkingDirectory(), "test-scenario");
    public static final File CHICAGO = new File(Locate.repoFolder(Pt2MatsimXML.class, "amodtaxi"), "src/test/resources/chicagoScenario");
    public static final File MINI = new File(Locate.repoFolder(Pt2MatsimXML.class, "amodtaxi"), "src/test/resources/miniScenario");
    public static final File SAN_FRANCISCO = new File(Locate.repoFolder(Pt2MatsimXML.class, "amodtaxi"), "src/test/resources/sanFranciscoScenario");
    public static final File TORONTO = new File(Locate.repoFolder(Pt2MatsimXML.class, "amodtaxi"), "src/test/resources/torontoScenario");
}
