package amodeus.amodtaxi.scenario.sanfrancisco;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import amodeus.amodeus.net.FastLinkLookup;
import amodeus.amodeus.net.MatsimAmodeusDatabase;
import amodeus.amodeus.options.ScenarioOptions;
import amodeus.amodeus.options.ScenarioOptionsBase;
import amodeus.amodeus.util.AmodeusTimeConvert;
import amodeus.amodeus.util.io.CopyFiles;
import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodeus.util.math.SI;
import amodeus.amodeus.util.matsim.NetworkLoader;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

import amodeus.amodtaxi.fleetconvert.SanFranciscoTripFleetConverter;
import amodeus.amodtaxi.fleetconvert.TripFleetConverter;
import amodeus.amodtaxi.scenario.FinishedScenario;
import amodeus.amodtaxi.scenario.Scenario;
import amodeus.amodtaxi.scenario.ScenarioLabels;
import amodeus.amodtaxi.scenario.TestDirectories;
import amodeus.amodtaxi.trace.DayTaxiRecord;
import amodeus.amodtaxi.trace.ReadTraceFiles;
import amodeus.amodtaxi.tripfilter.TaxiTripFilterCollection;
import amodeus.amodtaxi.tripfilter.TripNetworkFilter;
import amodeus.amodtaxi.tripmodif.NullModifier;
import ch.ethz.idsc.tensor.io.DeleteDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

public class SanFranciscoScenarioTest {
    private static final int NUM_TAXIS = 2;
    private static final AmodeusTimeConvert TIME_CONVERT = new AmodeusTimeConvert(ZoneId.of("America/Los_Angeles"));

    @BeforeClass
    public static void setup() throws Exception {
        GlobalAssert.that(TestDirectories.WORKING.mkdirs());
        SanFranciscoTestSetup.in(TestDirectories.WORKING);
    }

    @Test
    public void creationTest() throws Exception {
        List<File> traceFiles = TraceFileChoice.getDefault().random(NUM_TAXIS);
        Assert.assertFalse(traceFiles.isEmpty());

        /** prepare the network */
        File processingDir = new File(TestDirectories.WORKING, "Scenario");
        processingDir.mkdir();

        CopyFiles.now(TestDirectories.WORKING.getAbsolutePath(), processingDir.getAbsolutePath(), //
                Arrays.asList(ScenarioLabels.amodeusFile, ScenarioLabels.config, ScenarioLabels.network, ScenarioLabels.networkGz, ScenarioLabels.LPFile));
        Assert.assertTrue(new File(processingDir, ScenarioLabels.network).exists());
        Assert.assertTrue(new File(processingDir, ScenarioLabels.networkGz).exists());

        /** based on the taxi data, create a population and assemble a AMoDeus scenario */
        ScenarioOptions scenarioOptions = new ScenarioOptions(processingDir, ScenarioOptionsBase.getDefault());
        File configFile = new File(scenarioOptions.getPreparerConfigName());
        Assert.assertTrue(configFile.exists());
        Config configFull = ConfigUtils.loadConfig(configFile.toString());
        final Network network = NetworkLoader.fromNetworkFile(new File(processingDir, configFull.network().getInputFile()));
        Assert.assertFalse(network.getLinks().isEmpty()); // 16'882
        MatsimAmodeusDatabase db = MatsimAmodeusDatabase.initialize(network, scenarioOptions.getLocationSpec().referenceFrame());
        FastLinkLookup fll = new FastLinkLookup(network, db);

        /** get dayTaxiRecord from trace files */
        CsvFleetReaderSF reader = new CsvFleetReaderSF(new DayTaxiRecordSF(fll));
        DayTaxiRecord dayTaxiRecord = ReadTraceFiles.in(traceFiles, reader);
        Assert.assertEquals(NUM_TAXIS, dayTaxiRecord.numTaxis());

        LocalDate simulationDate = LocalDate.of(2008, 6, 4);
        TaxiTripFilterCollection tripFilter = new TaxiTripFilterCollection();
        /** trips which are faster than the network freeflow speeds would allow are removed */
        tripFilter.addFilter(new TripNetworkFilter(network, db, //
                Quantity.of(2.235200008, SI.VELOCITY), Quantity.of(3600, SI.SECOND), Quantity.of(200, SI.METER), true));

        /** prepare final scenario */
        File outputDirectory = new File(TestDirectories.WORKING, simulationDate.toString());
        TripFleetConverter converter = new SanFranciscoTripFleetConverter( //
                scenarioOptions, network, dayTaxiRecord, simulationDate, NullModifier.INSTANCE, tripFilter, outputDirectory);
        File finalTripsFile = Scenario.create(TestDirectories.WORKING, converter, processingDir, simulationDate, TIME_CONVERT);

        /** loading final trips */
        // List<TaxiTrip> finalTrips = ImportTaxiTrips.fromFile(finalTripsFile);
        // final int maxIter = 100;
        // new IterativeLinkSpeedEstimator(maxIter).compute(processingDir, network, db, finalTrips);

        FinishedScenario.copyToDir(processingDir.getAbsolutePath(), outputDirectory.getAbsolutePath(), //
                ScenarioLabels.amodeusFile, ScenarioLabels.networkGz, ScenarioLabels.populationGz, //
                ScenarioLabels.LPFile, ScenarioLabels.config, ScenarioLabels.linkSpeedData);

        Config createdConfig = ConfigUtils.loadConfig(new File(outputDirectory, ScenarioLabels.config).toString());
        Network createdNetwork = ScenarioUtils.loadScenario(createdConfig).getNetwork();
        Population createdPopulation = ScenarioUtils.loadScenario(createdConfig).getPopulation();

        Assert.assertFalse(createdNetwork.getLinks().isEmpty()); // 16'882
        Assert.assertFalse(createdPopulation.getPersons().isEmpty()); // 25
    }

    @AfterClass
    public static void cleanUp() throws Exception {
        DeleteDirectory.of(TestDirectories.WORKING, 3, 100);
    }
}
