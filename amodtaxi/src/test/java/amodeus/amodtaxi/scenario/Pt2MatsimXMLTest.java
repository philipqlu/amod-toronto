/* amodtaxi - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import amodeus.amodeus.util.io.CopyFiles;
import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodeus.util.matsim.xml.XmlCustomModifier;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.ethz.idsc.tensor.io.DeleteDirectory;

public class Pt2MatsimXMLTest {
    private File PT_FILE = new File(TestDirectories.WORKING, ScenarioLabels.pt2MatSettings);

    @BeforeClass
    public static void setUp() throws Exception {
        GlobalAssert.that(TestDirectories.WORKING.mkdirs());
        GlobalAssert.that(new File(TestDirectories.MINI, ScenarioLabels.pt2MatSettings).exists());
        /* Init */
        CopyFiles.now(TestDirectories.MINI.getAbsolutePath(), //
                TestDirectories.WORKING.getAbsolutePath(), Collections.singletonList(ScenarioLabels.pt2MatSettings));
    }

    @Test
    public void testLocalFileSystem() throws Exception {

        /* Run function of interest */
        Pt2MatsimXML.toLocalFileSystem(PT_FILE, TestDirectories.WORKING.getAbsolutePath());

        /* Check functionality */
        try (XmlCustomModifier xmlModifier = new XmlCustomModifier(PT_FILE)) {
            Document doc = xmlModifier.getDocument();
            Element rootNode = doc.getRootElement();
            Element module = rootNode.getChild("module");

            for (Element element : module.getChildren()) {
                String nameValue = element.getAttributeValue("name");
                if (nameValue == null)
                    continue;
                if (nameValue.equals("osmFile")) {
                    Assert.assertTrue(element.getAttributeValue("value").contains(TestDirectories.WORKING.getAbsolutePath()));
                }
                if (nameValue.equals("outputNetworkFile")) {
                    Assert.assertTrue(element.getAttributeValue("value").contains(TestDirectories.WORKING.getAbsolutePath()));
                }
            }
        }
    }

    @Test
    public void testChangeAttribute() throws Exception {
        /* Run function of interest */
        Map<String, String> map = new HashMap<>();
        map.put("outputCoordinateSystem", "EPSG:XXX");
        Pt2MatsimXML.changeAttributes(PT_FILE, map);

        /* Check functionality */
        try (XmlCustomModifier xmlModifier = new XmlCustomModifier(PT_FILE)) {
            Document doc = xmlModifier.getDocument();
            Element rootNode = doc.getRootElement();
            Element module = rootNode.getChild("module");

            for (Element element : module.getChildren()) {
                String nameValue = element.getAttributeValue("name");
                if (nameValue == null)
                    continue;
                if (nameValue.equals("outputCoordinateSystem"))
                    Assert.assertEquals("EPSG:XXX", element.getAttributeValue("value"));
            }
        }
    }

    @AfterClass
    public static void cleanUp() throws Exception {
        DeleteDirectory.of(TestDirectories.WORKING, 2, 14);
    }
}
