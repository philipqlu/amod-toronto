/* amodtaxi - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import amodeus.amodeus.util.io.CopyFiles;
import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodeus.util.matsim.NetworkLoader;
import amodeus.amodtaxi.osm.StaticMapCreator;
import ch.ethz.idsc.tensor.io.DeleteDirectory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.api.core.v01.network.Network;

public class StaticMapCreatorTest {
    @BeforeClass
    public static void setup() throws Exception {
        GlobalAssert.that(TestDirectories.WORKING.mkdirs());

        CopyFiles.now(TestDirectories.MINI.getAbsolutePath(), TestDirectories.WORKING.getAbsolutePath(), //
                Arrays.asList(ScenarioLabels.osmData, ScenarioLabels.amodeusFile, ScenarioLabels.pt2MatSettings), true);
        /** change pt2Matsim settings to local file system */
        Pt2MatsimXML.toLocalFileSystem(new File(TestDirectories.WORKING, ScenarioLabels.pt2MatSettings), TestDirectories.WORKING.getAbsolutePath());
    }

    @Test
    public void test() throws IOException {
        /* Run function of interest */
        StaticMapCreator.now(TestDirectories.WORKING);

        /* Check functionality */
        File networkFile = new File(TestDirectories.WORKING, "networkPt2Matsim.xml.gz");
        Assert.assertTrue(networkFile.exists());

        Network network = NetworkLoader.fromNetworkFile(networkFile);
        Network referenceNetwork = NetworkLoader.fromNetworkFile(new File(TestDirectories.MINI, "networkPt2Matsim.xml.gz"));
        Assert.assertEquals(referenceNetwork.getLinks().size(), network.getLinks().size());
    }

    @AfterClass
    public static void cleanUp() throws Exception {
        DeleteDirectory.of(TestDirectories.WORKING, 2, 5);
    }
}