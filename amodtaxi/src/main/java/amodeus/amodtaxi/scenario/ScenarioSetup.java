/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import amodeus.amodeus.util.io.CopyFiles;
import amodeus.amodtaxi.util.ResourceHandling;

public enum ScenarioSetup {
    ;

    public static void in(File workingDir, File resourcesDir, String... additionals) throws Exception {
        /** copy relevant files containing settings for scenario generation */
        CopyFiles.now(resourcesDir.getAbsolutePath(), workingDir.getAbsolutePath(), //
                Arrays.asList(ScenarioLabels.config, ScenarioLabels.pt2MatSettings, ScenarioLabels.LPFile), true);

        /** copy additional files */
        CopyFiles.now(resourcesDir.getAbsolutePath(), workingDir.getAbsolutePath(), Arrays.asList(additionals), true);

        /** AmodeusOptions.properties is not replaced as it might be changed by user during
         * scenario generation process. */
        if (!new File(workingDir, ScenarioLabels.amodeusFile).exists())
            CopyFiles.now(resourcesDir.getAbsolutePath(), workingDir.getAbsolutePath(), //
                    Collections.singletonList(ScenarioLabels.amodeusFile), false);

        /** change pt2Matsim settings to local file system */
        Pt2MatsimXML.toLocalFileSystem(new File(workingDir, ScenarioLabels.pt2MatSettings), //
                workingDir.getAbsolutePath());
    }

    public static void in(File workingDir, String scenario, String... additionals) throws Exception {
        /** copy relevant files containing settings for scenario generation */
        for (String file : Arrays.asList(ScenarioLabels.config, ScenarioLabels.pt2MatSettings, ScenarioLabels.LPFile))
            ResourceHandling.copy(scenario + "/" + file, Path.of(workingDir.getAbsolutePath(), file), true);

        /** copy additional files */
        for (String file : additionals)
            ResourceHandling.copy(scenario + "/" + file, Path.of(workingDir.getAbsolutePath(), file), true);

        /** AmodeusOptions.properties is not replaced as it might be changed by user during
         * scenario generation process. */
        if (!new File(workingDir, ScenarioLabels.amodeusFile).exists())
            ResourceHandling.copy(scenario + "/" + ScenarioLabels.amodeusFile, Path.of(workingDir.getAbsolutePath(), ScenarioLabels.amodeusFile), false);

        /** change pt2Matsim settings to local file system */
        Pt2MatsimXML.toLocalFileSystem(new File(workingDir, ScenarioLabels.pt2MatSettings), //
                workingDir.getAbsolutePath());
    }
}
