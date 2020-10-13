/* amodtaxi - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.chicago;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import amodeus.amodeus.options.ScenarioOptionsBase;
import amodeus.amodeus.util.math.GlobalAssert;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import amodeus.amodtaxi.scenario.ScenarioLabels;
import amodeus.amodtaxi.scenario.TestDirectories;
import ch.ethz.idsc.tensor.io.DeleteDirectory;

/** Tests if data for the creation of the Chicago taxi scenario is accessible from the
 * web API. */
public class ChicagoDataLoaderTest {
    @BeforeClass
    public static void setUp() throws Exception {
        GlobalAssert.that(TestDirectories.WORKING.mkdirs());
        GlobalAssert.that(new File(TestDirectories.CHICAGO, ScenarioLabels.amodeusFile).exists());

        /* Reduce population size in Properties */
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(new File(TestDirectories.CHICAGO, ScenarioLabels.amodeusFile))) {
            properties.load(fis);
        }
        properties.setProperty(ScenarioOptionsBase.MAXPOPULATIONSIZEIDENTIFIER, "100");
        try (FileOutputStream out = new FileOutputStream(new File(TestDirectories.WORKING, ScenarioLabels.amodeusFile))) {
            properties.store(out, null);
        }
    }

    @Test
    public void test() throws Exception {
        /* Check ChicagoDataLoader */
        File tripFile = ChicagoDataLoader.from(ScenarioLabels.amodeusFile, TestDirectories.WORKING);
        Assert.assertTrue(tripFile.exists());
    }

    @AfterClass
    public static void cleanUp() throws Exception {
        DeleteDirectory.of(TestDirectories.WORKING, 2, 5);
    }
}
