/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.sanfrancisco;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import amodeus.amodeus.util.io.MultiFileReader;
import amodeus.amodeus.util.io.MultiFileTools;
import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodtaxi.util.RandomElements;
import amodeus.amodtaxi.util.ResourceHandling;

public class TraceFileChoice {
    public static final File DEFAULT_DATA = new File(MultiFileTools.getDefaultWorkingDirectory(), "default_cabspottingdata");

    public static TraceFileChoice getOrDefault(File directory, String sharedFileName) {
        try {
            return get(directory, sharedFileName);
        } catch (RuntimeException e1) {
            try {
                System.err.println("Unable to find specified files; proceeding with default mini-batch.\n" //
                        + "Full data by by Michal Piorkowski, Natasa Sarafijanovic-Djukic, and Matthias Grossglauser can be downloaded from: " //
                        + new URL("https://crawdad.org/epfl/mobility/20090224/"));
            } catch (MalformedURLException e2) {
                e2.printStackTrace();
            }
            return getDefault();
        }
    }

    public static TraceFileChoice getDefault() {
        try {
            DEFAULT_DATA.mkdirs();
            ResourceHandling.copy("sanFranciscoScenario/cabspottingdata/new_abboip.txt", Path.of(DEFAULT_DATA.getAbsolutePath(), "new_abboip.txt"), false);
            ResourceHandling.copy("sanFranciscoScenario/cabspottingdata/new_abcoij.txt", Path.of(DEFAULT_DATA.getAbsolutePath(), "new_abcoij.txt"), false);
            ResourceHandling.copy("sanFranciscoScenario/cabspottingdata/_cabs.txt", Path.of(DEFAULT_DATA.getAbsolutePath(), "_cabs.txt"), false);
            ResourceHandling.copy("sanFranciscoScenario/cabspottingdata/README", Path.of(DEFAULT_DATA.getAbsolutePath(), "README"), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return get(DEFAULT_DATA, "new_");
        // File testScenario = new File(Locate.repoFolder(TraceFileChoice.class, "amodtaxi"), "resources/sanFranciscoScenario");
        // return get(new File(testScenario, "cabspottingdata"), "new_");
    }

    public static TraceFileChoice get(File directory, String sharedFileName) {
        List<File> taxiFiles = new MultiFileReader(directory, sharedFileName).getFolderFiles();
        return new TraceFileChoice(taxiFiles);
    }

    // ---

    private final List<File> taxiFiles;

    private TraceFileChoice(List<File> taxiFiles) {
        GlobalAssert.that(taxiFiles.size() > 0);
        this.taxiFiles = taxiFiles;
    }

    /** @return all trace files containing the @param names in their name , sample usage
     *         List<File> traceFiles = (new TraceFileChoice(taxiFiles)).specified("new_aupclik","new_ojumna") */
    public List<File> specified(String... nameSegments) {
        Predicate<File> inNameSegments = file -> Arrays.stream(nameSegments).anyMatch(file.getName()::contains);
        return taxiFiles.stream().filter(inNameSegments).collect(Collectors.toList());
    }

    /** @return random choice of @param numTaxiTraces files */
    public List<File> random(int numTaxiTraces) {
        return RandomElements.of(new ArrayList<>(taxiFiles), numTaxiTraces);
    }
}
