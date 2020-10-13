/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import amodeus.amodeus.util.math.GlobalAssert;
import ch.ethz.idsc.tensor.io.DeleteDirectory;

public enum FinishedScenario {
    ;

    public static void copyToDir(String processingDir, String destinDir, String... fileNames) throws IOException {
        System.out.println("Copying scenario from : " + processingDir);
        System.out.println("to :                    " + destinDir);

        File destinDirFile = new File(destinDir);
        if (destinDirFile.exists())
            DeleteDirectory.of(destinDirFile, 2, 10);
        destinDirFile.mkdir();
        GlobalAssert.that(destinDirFile.isDirectory());

        { // files from processing directory
            for (String fileName : fileNames) {
                Path source = Paths.get(processingDir, fileName);
                Path target = Paths.get(destinDir, fileName);
                try {
                    Files.copy(source, target /* , options */);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
