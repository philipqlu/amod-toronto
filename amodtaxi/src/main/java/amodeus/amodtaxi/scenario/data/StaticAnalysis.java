package amodeus.amodtaxi.scenario.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import amodeus.amodeus.net.FastLinkLookup;
import amodeus.amodeus.net.MatsimAmodeusDatabase;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.dvrp.router.DistanceAsTravelDisutility;
import org.matsim.core.router.FastAStarLandmarksFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.trafficmonitoring.FreeSpeedTravelTime;

public class StaticAnalysis {
    private static final String ALL = "All";

    private final LeastCostPathCalculator leastCostPathCalculator;
    private final FastLinkLookup fastLinkLookup;
    private final TaxiStampReader taxiStampReader;
    private Map<String, Summary> summaries = new HashMap<>();

    public StaticAnalysis(MatsimAmodeusDatabase db, Network network, TaxiStampReader taxiStampReader) {
        this(new FastLinkLookup(network, db), network, taxiStampReader);
    }

    public StaticAnalysis(FastLinkLookup fastLinkLookup, Network network, TaxiStampReader taxiStampReader) {
        this(fastLinkLookup, //
                new FastAStarLandmarksFactory(Runtime.getRuntime().availableProcessors()) //
                .createPathCalculator(network, new DistanceAsTravelDisutility(), new FreeSpeedTravelTime()), //
                taxiStampReader);
    }

    public StaticAnalysis(FastLinkLookup fastLinkLookup, LeastCostPathCalculator leastCostPathCalculator, TaxiStampReader taxiStampReader) {
        this.fastLinkLookup = fastLinkLookup;
        this.leastCostPathCalculator = leastCostPathCalculator;
        this.taxiStampReader = taxiStampReader;
    }

    public void of(File... files) {
        of(Arrays.asList(files));
    }

    public void of(Collection<File> files) {
        System.out.println("found " + files.size() + " files to analyze");
        AtomicInteger ai = new AtomicInteger();
        Summary summaryAll = Summary.of(files.stream().map(file -> {
            System.out.println("analyzing file (" + ai.incrementAndGet() + "/" + files.size() + "): " + file.getAbsolutePath());
            try {
                return Summary.of(file, taxiStampReader, fastLinkLookup, leastCostPathCalculator);
            } catch (Exception e) {
                System.err.println("unable to read " + file.getAbsolutePath());
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList()));
        summaries.put(ALL, summaryAll);

        summaryAll.dates().forEach(date -> summaries.put(date.toString(), summaryAll.on(date)));
    }

    public void saveTo(File directory) {
        /** prepare folder to save information */
        directory.mkdir();

        /** write the analyzed information */
        summaries.forEach((name, summary) -> {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(directory, name))))) {
                bufferedWriter.write(summary.toString());
            } catch (IOException e) {
                System.err.println("unable to write " + new File(directory, name).getAbsolutePath());
                e.printStackTrace();
            }
        });
    }

    public Summary getSummary() {
        return summaries.get(ALL);
    }

    public Summary getSummary(LocalDate date) {
        return summaries.get(date.toString());
    }
}
