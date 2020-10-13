package amodeus.amodtaxi.scenario.sanfrancisco;

import java.io.File;
import java.io.IOException;
import java.util.List;

import amodeus.amodeus.util.io.Locate;
import ch.ethz.idsc.tensor.io.DeleteDirectory;
import junit.framework.TestCase;

public class TraceFileChoiceTest extends TestCase {
    public void testGet() {
        File dataFile = new File(Locate.repoFolder(TraceFileChoiceTest.class, "amodtaxi"), "src/main/resources/sanFranciscoScenario/cabspottingdata");
        List<File> traceFiles = TraceFileChoice.get(dataFile, "new_").random(2);
        assertEquals(2, traceFiles.size());
    }

    public void testGetOrDefault() {
        List<File> traceFiles = TraceFileChoice.getOrDefault(new File("404"), "new_").random(2);
        assertEquals(2, traceFiles.size());
    }

    public void testFail() {
        try {
            TraceFileChoice.get(new File("404"), "new_");
            fail();
        } catch (RuntimeException e) {
            //
        }
    }

    @Override
    public void tearDown() throws IOException {
        if (TraceFileChoice.DEFAULT_DATA.exists())
            DeleteDirectory.of(TraceFileChoice.DEFAULT_DATA, 1, 5);
    }
}
