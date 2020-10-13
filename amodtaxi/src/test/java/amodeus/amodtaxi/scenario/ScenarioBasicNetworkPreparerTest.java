/* amodtaxi - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario;

import java.io.File;
import java.util.Arrays;

import amodeus.amodeus.util.io.CopyFiles;
import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodeus.util.matsim.NetworkLoader;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.ethz.idsc.tensor.io.DeleteDirectory;
import org.matsim.api.core.v01.network.Network;

public class ScenarioBasicNetworkPreparerTest {
    @BeforeClass
    public static void setup() throws Exception {
        GlobalAssert.that(TestDirectories.WORKING.mkdirs());
        CopyFiles.now(TestDirectories.MINI.getAbsolutePath(), TestDirectories.WORKING.getAbsolutePath(), //
                Arrays.asList(ScenarioLabels.pt2MatSettings, "networkPt2Matsim.xml.gz"), true);
        /** change pt2Matsim settings to local file system */
        Pt2MatsimXML.toLocalFileSystem(new File(TestDirectories.WORKING, ScenarioLabels.pt2MatSettings), TestDirectories.WORKING.getAbsolutePath());
    }

    @Test
    public void test() {
        /* Run function of interest */
        ScenarioBasicNetworkPreparer.run(TestDirectories.WORKING);

        /* Check functionality */
        Assert.assertTrue(new File(TestDirectories.WORKING, ScenarioLabels.network).exists());
        Assert.assertTrue(new File(TestDirectories.WORKING, ScenarioLabels.networkGz).exists());

        Network network = NetworkLoader.fromNetworkFile(new File(TestDirectories.WORKING, ScenarioLabels.networkGz));
        Assert.assertFalse(network.getLinks().isEmpty());
    }

    @AfterClass
    public static void cleanUp() throws Exception {
        DeleteDirectory.of(TestDirectories.WORKING, 2, 5);
    }
}