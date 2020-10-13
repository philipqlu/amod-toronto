/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.sanfrancisco;

import java.io.File;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import amodeus.amodeus.analysis.Analysis;
import amodeus.amodeus.linkspeed.LinkSpeedUtils;
import amodeus.amodeus.net.FastLinkLookup;
import amodeus.amodeus.net.MatsimAmodeusDatabase;
import amodeus.amodeus.options.ScenarioOptions;
import amodeus.amodeus.options.ScenarioOptionsBase;
import amodeus.amodeus.taxitrip.ExportTaxiTrips;
import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodeus.util.AmodeusTimeConvert;
import amodeus.amodeus.util.math.GlobalAssert;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

import amodeus.amodtaxi.linkspeed.iterative.IterativeLinkSpeedEstimator;
import amodeus.amodtaxi.scenario.AllTaxiTrips;
import amodeus.amodtaxi.trace.DayTaxiRecord;
import amodeus.amodtaxi.tripfilter.TaxiTripFilterCollection;
import ch.ethz.idsc.tensor.Scalar;

@Deprecated
/* package */ class StandaloneFleetConverterSF {
    private final File workingDirectory;
    private final MatsimAmodeusDatabase db;
    private final Network network;
    private final File configFile;
    private final Config configFull;
    private final int maxIter = 100; // 10'000 need 1 hour // TODO make customizable
    private DayTaxiRecord dayTaxiRecord;
    private ScenarioOptions simOptions;
    private final TaxiTripFilterCollection speedEstimationTripFilter;
    private final TaxiTripFilterCollection populationTripFilter;

    private final FastLinkLookup fastLinkLookup;
    private File outputDirectory;
    private final AmodeusTimeConvert timeConvert;

    private final Scalar timeStep;

    public StandaloneFleetConverterSF(File workingDirectory, DayTaxiRecord dayTaxiRecord, //
            MatsimAmodeusDatabase db, Network network, Scalar timeStep, //
            AmodeusTimeConvert timeConvert, TaxiTripFilterCollection taxiTripFilter, //
            TaxiTripFilterCollection populationTripFilter) throws Exception {
        this.workingDirectory = workingDirectory;
        this.dayTaxiRecord = dayTaxiRecord;
        this.db = db;
        this.timeStep = timeStep;
        this.timeConvert = timeConvert;
        this.speedEstimationTripFilter = taxiTripFilter;
        this.populationTripFilter = populationTripFilter;
        simOptions = new ScenarioOptions(workingDirectory, ScenarioOptionsBase.getDefault());
        configFile = new File(simOptions.getPreparerConfigName());
        GlobalAssert.that(configFile.exists());
        configFull = ConfigUtils.loadConfig(configFile.toString());
        this.network = network;
        GlobalAssert.that(Objects.nonNull(network));
        fastLinkLookup = new FastLinkLookup(network, db);
    }

    public void run(LocalDate simulationDate) throws Exception {
        /** STEP 0: Prepare Environment and load all configuration files */
        outputDirectory = StaticHelper.prepareFolder(workingDirectory, new File(workingDirectory, configFull.controler().getOutputDirectory()));

        /** STEP 1: Generate simobjs from daytaxirecords for visualization */
        SimulationFleetDumperSF sfd = new SimulationFleetDumperSF(fastLinkLookup, timeStep, timeConvert);
        outputDirectory.mkdirs();
        GlobalAssert.that(outputDirectory.isDirectory());
        sfd.createDumpOf(dayTaxiRecord, outputDirectory, simulationDate);

        /** STEP 2: Find relevant trips */
        Collection<TaxiTrip> tripsAll = AllTaxiTrips.in(dayTaxiRecord).on(simulationDate);

        /** STEP 3: Filter trips which are unwanted for speed estimation (not meaningful durations) */
        List<TaxiTrip> tripsSpeedEstimation = speedEstimationTripFilter.filterStream(tripsAll.stream()).collect(Collectors.toList());
        speedEstimationTripFilter.printSummary();
        System.out.println("Trips for speed estimation: " + tripsSpeedEstimation.size());

        /** STEP 4: Export final taxi trips and estimation trip population */
        ExportTaxiTrips.toFile(tripsSpeedEstimation.stream(), new File(workingDirectory, "finalTripsEstimation.csv"));
        AdamAndEve.create(workingDirectory, tripsSpeedEstimation, network, fastLinkLookup, timeConvert, simulationDate, "_speedEst");

        // taxiTripFilter.
        // ClosestLinkSelect linkSelect = new ClosestLinkSelect(db, qt);
        // AverageNetworkSpeed speedFilter = new AverageNetworkSpeed(network, linkSelect, simulationDate, timeConvert);
        // List<TaxiTrip> trips = tripsAll.stream().filter(t -> speedFilter.isBelow(t, maxAverageSpeed)).collect(Collectors.toList());

        /** STEP 5: Generate population.xml using the recordings */
        System.out.println("Trips before filtering: " + tripsAll.size());
        List<TaxiTrip> tripsForPopulation = populationTripFilter.filterStream(tripsAll.stream()).collect(Collectors.toList());
        System.out.println("Trips after filtering:  " + tripsForPopulation.size());
        populationTripFilter.printSummary();

        AdamAndEve.create(workingDirectory, tripsForPopulation, network, fastLinkLookup, timeConvert, simulationDate, "");
        ExportTaxiTrips.toFile(tripsForPopulation.stream(), new File(workingDirectory, "finalTripsPopulation.csv"));

        /** STEP 6: Generate the report */
        try {
            Analysis analysis = Analysis.setup(simOptions, outputDirectory, network, db);
            analysis.run();
        } catch (Exception exception) {
            System.err.println("could not produce report, analysis failed.");
            exception.printStackTrace();
        }
        /** STEP 7: Create Link Speed Data File */
        try {
            // other tested options...
            // TaxiLinkSpeedEstimator lsCalc = new ConventionalLinkSpeedCalculator(db, dayTaxiRecord, simulationDate, network, timeConvert);
            // TaxiLinkSpeedEstimator lsCalc = new FlowLinkSpeed(trips, network, timeConvert, db, qt, simulationDate);
            // TaxiLinkSpeedEstimator lsCalc = new FlowTimeInvLinkSpeed(trips, network, db, GLPKLinOptDelayCalculator.INSTANCE);

            // iterative
            IterativeLinkSpeedEstimator lsCalc = new IterativeLinkSpeedEstimator(maxIter, new Random(123));
            lsCalc.compute(workingDirectory, network, db, tripsSpeedEstimation);

            File linkSpeedsFile = new File(simOptions.getLinkSpeedDataName() + "");
            LinkSpeedUtils.writeLinkSpeedData(linkSpeedsFile, lsCalc.getLsData());
        } catch (Exception exception) {
            System.err.println("could not generate link speed data...");
            exception.printStackTrace();
        }
    }
}