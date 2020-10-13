/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.toronto;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import amodeus.amodeus.options.ScenarioOptionsBase;
import amodeus.amodeus.util.math.GlobalAssert;

public enum TorontoDataLoader {
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
	
	private static File from(Properties properties, File dir, int entryLimit) throws Exception {
		File file = null;
		try {
			String urlString = properties.getProperty("URL") + "?$limit=" + entryLimit;
			URL url = new URL(urlString);
			System.out.println("INFO download data from " + url);
			
			/* Download file to local directory */
			try (InputStream in = url.openStream()) {
				String date = properties.getProperty("date").replace("/", "_");
				file = new File(dir, "taxi_trips_" + date + ".csv");
				Files.copy(in,  file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				System.out.println("INFO successfully copied data to " + file.getAbsolutePath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return file;
	}

}
