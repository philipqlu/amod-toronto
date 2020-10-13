/* amodtaxi - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario;

import java.io.File;

import amodeus.amodeus.util.math.GlobalAssert;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.ethz.idsc.tensor.io.DeleteDirectory;

public class ScenarioSetupTest {
    @BeforeClass
    public static void setUp() {
        GlobalAssert.that(TestDirectories.WORKING.mkdirs());
    }

    @Test
    public void test() throws Exception {
        /* Run function of interest */
        ScenarioSetup.in(TestDirectories.WORKING, TestDirectories.MINI);

        /* Check functionality */
        Assert.assertTrue(new File(TestDirectories.WORKING, ScenarioLabels.config).exists());
        Assert.assertTrue(new File(TestDirectories.WORKING, ScenarioLabels.pt2MatSettings).exists());
        Assert.assertTrue(new File(TestDirectories.WORKING, ScenarioLabels.LPFile).exists());
        Assert.assertTrue(new File(TestDirectories.WORKING, ScenarioLabels.amodeusFile).exists());
    }

    @AfterClass
    public static void cleanUp() throws Exception {
        DeleteDirectory.of(TestDirectories.WORKING, 2, 14);
    }
}
