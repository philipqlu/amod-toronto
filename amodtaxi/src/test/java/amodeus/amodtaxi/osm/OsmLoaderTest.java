/* amodtaxi - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.osm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Properties;

import amodeus.amodeus.util.math.GlobalAssert;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import amodeus.amodtaxi.scenario.ScenarioLabels;
import amodeus.amodtaxi.scenario.TestDirectories;
import ch.ethz.idsc.tensor.io.DeleteDirectory;

public class OsmLoaderTest {
    @BeforeClass
    public static void setUp() throws Exception {
        GlobalAssert.that(TestDirectories.WORKING.mkdirs());

        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(new File(TestDirectories.MINI, ScenarioLabels.amodeusFile))) {
            properties.load(fis);
        }
        properties.setProperty("boundingBox", "{8.54773, 47.37697, 8.54952, 47.37916}"); // test box with area of ETH
        try (FileOutputStream out = new FileOutputStream(new File(TestDirectories.WORKING, ScenarioLabels.amodeusFile))) {
            properties.store(out, null);
        }
    }

    @Test
    public void test() throws Exception {
        File osmFile = new File(TestDirectories.WORKING, ScenarioLabels.osmData);

        /* Run function of interest */
        OsmLoader osmLoader = OsmLoader.of(new File(TestDirectories.WORKING, ScenarioLabels.amodeusFile));
        osmLoader.saveIfNotAlreadyExists(osmFile);

        /* Check functionality */
        try ( //
                FileReader fileReader = new FileReader(osmFile);
                BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            Assert.assertTrue(bufferedReader.lines().anyMatch(line -> line.contains("ETH/Universitätsspital")));
        }
    }

    @AfterClass
    public static void cleanUp() throws Exception {
        DeleteDirectory.of(TestDirectories.WORKING, 2, 14);
    }
}
