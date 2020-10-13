/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.osm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

import amodeus.amodeus.util.math.GlobalAssert;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class OsmLoader {
    public static OsmLoader of(File propertiesFile) throws FileNotFoundException, IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(propertiesFile)) {
            props.load(fis);
        }
        GlobalAssert.that(propertiesFile.exists());
        System.out.println(propertiesFile.getAbsolutePath());
        Tensor boundBox = Tensors.fromString(props.getProperty("boundingBox"));
        return new OsmLoader( //
                boundBox.Get(0).number().doubleValue(), //
                boundBox.Get(1).number().doubleValue(), //
                boundBox.Get(2).number().doubleValue(), //
                boundBox.Get(3).number().doubleValue());
    }

    private final double[] bbox;

    private OsmLoader(double minLong, double minLat, double maxLong, double maxLat) {
        bbox = new double[] { minLong, minLat, maxLong, maxLat };
        checkBbox();
    }

    private void checkBbox() {
        GlobalAssert.that(bbox.length == 4);
        GlobalAssert.that(bbox[0] < bbox[2] && bbox[1] < bbox[3]);
    }

    private URL getURL() throws MalformedURLException {
        return new URL("https://overpass-api.de/api/map?bbox=" + Arrays.stream(bbox) //
                .mapToObj(String::valueOf).map(Object::toString).collect(Collectors.joining(",")));
    }

    public void saveTo(File file) {
        try {
            URL url = getURL();
            System.out.println("INFO download data from " + url);
            InputStream in = url.openStream();
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveIfNotAlreadyExists(File file) {
        if (file.exists())
            System.err.println("INFO " + file.getAbsolutePath() + " already exists");
        else
            saveTo(file);
    }
}
