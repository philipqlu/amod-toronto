/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.chicago;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import amodeus.amodeus.options.ScenarioOptionsBase;
import amodeus.amodeus.util.math.GlobalAssert;

public enum ChicagoDataLoader {
    ;

    public static File from(String propertiesName, File dir) throws Exception {
        GlobalAssert.that(dir.isDirectory());
        File propertiesFile = new File(dir, propertiesName);
        GlobalAssert.that(propertiesFile.isFile());

        /* Load properties */
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(propertiesFile)) {
            properties.load(fis);
        }

        return from(properties, dir, Integer.valueOf(properties.getProperty(ScenarioOptionsBase.MAXPOPULATIONSIZEIDENTIFIER)));
    }

    private static File from(Properties properties, File dir, int entryLimit) {
        File file = null;
        try {
            URL url = getURL(properties, entryLimit);
            System.out.println("INFO download data from " + url);
            try (InputStream in = url.openStream()) {
                file = createFile(properties, dir);
                Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("INFO successfully copied data to " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private static URL getURL(Properties properties, int entryLimit) throws Exception {
        Date date = ScenarioConstants.inFormat.parse(properties.getProperty("date"));
        String date1 = ScenarioConstants.outFormat.format(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, 24);
        String date2 = ScenarioConstants.outFormat.format(cal.getTime());
        String urlString = properties.get("URL") + "?$where=trip_start_timestamp%20between%20%27" + date1 + //
                "%27%20and%20%27" + date2 + "%27&$limit=" + entryLimit;
        System.out.println("URLString: " + urlString);
        return new URL(urlString);
    }

    private static File createFile(Properties properties, File dir) {
        GlobalAssert.that(dir.isDirectory());
        String date = properties.getProperty("date").replace("/", "_");
        return new File(dir, "Taxi_Trips_" + date + ".csv");
    }
}
