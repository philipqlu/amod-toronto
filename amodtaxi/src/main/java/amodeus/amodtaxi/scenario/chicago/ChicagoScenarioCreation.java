/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.chicago;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import amodeus.amodeus.net.FastLinkLookup;
import amodeus.amodeus.net.MatsimAmodeusDatabase;
import amodeus.amodeus.options.ScenarioOptions;
import amodeus.amodeus.options.ScenarioOptionsBase;
import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodeus.util.AmodeusTimeConvert;
import amodeus.amodeus.util.io.CopyFiles;
import amodeus.amodeus.util.io.MultiFileTools;
import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodeus.util.math.SI;
import amodeus.amodeus.util.matsim.NetworkLoader;
import amodeus.amodtaxi.scenario.ScenarioCreation;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

import amodeus.amodtaxi.fleetconvert.ChicagoOnlineTripFleetConverter;
import amodeus.amodtaxi.fleetconvert.TripFleetConverter;
import amodeus.amodtaxi.osm.StaticMapCreator;
import amodeus.amodtaxi.scenario.FinishedScenario;
import amodeus.amodtaxi.scenario.Scenario;
import amodeus.amodtaxi.scenario.ScenarioBasicNetworkPreparer;
import amodeus.amodtaxi.scenario.ScenarioLabels;
import amodeus.amodtaxi.scenario.TaxiTripsReader;
import amodeus.amodtaxi.tripfilter.TaxiTripFilterCollection;
import amodeus.amodtaxi.tripfilter.TripNetworkFilter;
import amodeus.amodtaxi.tripmodif.ChicagoFormatModifier;
import amodeus.amodtaxi.tripmodif.OriginDestinationCentroidResampling;
import amodeus.amodtaxi.tripmodif.TaxiDataModifier;
import amodeus.amodtaxi.tripmodif.TaxiDataModifierCollection;
import amodeus.amodtaxi.tripmodif.TripStartTimeShiftResampling;
import amodeus.amodtaxi.util.LocalDateConvert;
import ch.ethz.idsc.tensor.io.DeleteDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

public class ChicagoScenarioCreation extends ScenarioCreation {
    private static final AmodeusTimeConvert TIME_CONVERT = new AmodeusTimeConvert(ZoneId.of("America/Chicago"));
    // TODO load random with a seed over scenarioOptions
    private static final Random RANDOM = new Random(123);

    /** This main function will create an AMoDeus scenario based on the Chicago taxi
     * dataset available online in the current working directory. Settings can afterwards
     * be changed in the AmodeusOptions.properties file located in the directory.
     *
     * @throws Exception */
    public static void main(String[] args) throws Exception {
        ChicagoScenarioCreation scenario = ChicagoScenarioCreation.in(MultiFileTools.getDefaultWorkingDirectory());
        scenario.linkSpeedData(100_000);
    }

    // ---

    public static ChicagoScenarioCreation in(File workingDirectory) throws Exception {
        ChicagoSetup.in(workingDirectory);

        /* Download of open street map data to create scenario */
        StaticMapCreator.now(workingDirectory);

        /* Prepare the network */
        ScenarioBasicNetworkPreparer.run(workingDirectory);

        /* Load taxi data for the city of Chicago */
        File tripFile = ChicagoDataLoader.from(ScenarioLabels.amodeusFile, workingDirectory);

        /* Create empty scenario folder */
        File processingDir = new File(workingDirectory, "Scenario");
        if (processingDir.isDirectory())
            DeleteDirectory.of(processingDir, 2, 50);
        if (!processingDir.isDirectory())
            processingDir.mkdir();

        CopyFiles.now(workingDirectory.getAbsolutePath(), processingDir.getAbsolutePath(), Arrays.asList(//
                ScenarioLabels.amodeusFile, //
                ScenarioLabels.config, //
                ScenarioLabels.network, //
                ScenarioLabels.networkGz, //
                ScenarioLabels.LPFile));
        ScenarioOptions scenarioOptions = new ScenarioOptions(processingDir, //
                ScenarioOptionsBase.getDefault());
        LocalDate simulationDate = LocalDateConvert.ofOptions(scenarioOptions.getString("date"));

        /* Based on the taxi data, create a population and assemble a AMoDeus scenario */
        File configFile = new File(scenarioOptions.getPreparerConfigName());
        GlobalAssert.that(configFile.exists());
        Config configFull = ConfigUtils.loadConfig(configFile.toString());
        final Network network = NetworkLoader.fromNetworkFile(new File(processingDir, configFull.network().getInputFile()));
        MatsimAmodeusDatabase db = MatsimAmodeusDatabase.initialize(network, scenarioOptions.getLocationSpec().referenceFrame());
        FastLinkLookup fll = new FastLinkLookup(network, db);

        /* Prepare for creation of scenario */
        TaxiTripsReader tripsReader = new OnlineTripsReaderChicago();
        TaxiDataModifier tripModifier;

        TaxiDataModifierCollection taxiDataModifierCollection = new TaxiDataModifierCollection();
        /** below filter was removed as it causes request spikes at quarter hour intervals,
         * see class for detailed description */
        // addModifier(new ChicagoTripStartTimeResampling(random));
        /** instead the TripStartTimeShiftResampling is used: */
        taxiDataModifierCollection.addModifier(new TripStartTimeShiftResampling(RANDOM, Quantity.of(900, SI.SECOND)));
        /** TODO RVM document why centroid resampling */
        File vNFile = new File(processingDir, "virtualNetworkChicago");
        taxiDataModifierCollection.addModifier(new OriginDestinationCentroidResampling(RANDOM, network, fll, vNFile));
        tripModifier = taxiDataModifierCollection;

        TaxiTripFilterCollection taxiTripFilterCollection = new TaxiTripFilterCollection();
        /** trips which are faster than the network freeflow speeds would allow are removed */
        taxiTripFilterCollection.addFilter(new TripNetworkFilter(network, db, //
                Quantity.of(2.235200008, "m*s^-1"), Quantity.of(3600, "s"), Quantity.of(200, "m"), true));

        // TODO eventually remove, this did not improve the fit.
        // finalFilters.addFilter(new TripMaxSpeedFilter(network, db, ScenarioConstants.maxAllowedSpeed));
        File destinDir = new File(workingDirectory, "CreatedScenario");
        List<TaxiTrip> finalTrips;

        // prepare final scenario
        TripFleetConverter converter = //
                new ChicagoOnlineTripFleetConverter(scenarioOptions, network, tripModifier, //
                        new ChicagoFormatModifier(), taxiTripFilterCollection, tripsReader, tripFile, new File(processingDir, "tripData"));
        File finalTripsFile = Objects.requireNonNull(Scenario.create(workingDirectory, tripFile, converter, processingDir, simulationDate, TIME_CONVERT));

        System.out.println("The final trips file is: " + finalTripsFile.getAbsolutePath());

        FinishedScenario.copyToDir(processingDir.getAbsolutePath(), //
                destinDir.getAbsolutePath(), //
                ScenarioLabels.amodeusFile, ScenarioLabels.networkGz, ScenarioLabels.populationGz, //
                ScenarioLabels.LPFile, ScenarioLabels.config, "virtualNetworkChicago");
        cleanUp(workingDirectory);
        return new ChicagoScenarioCreation(network, db, finalTripsFile, destinDir);
    }

    private static void cleanUp(File workingDir) {
        /** delete unneeded files */
        // DeleteDirectory.of(new File(workingDir, "Scenario"), 2, 14);
        // DeleteDirectory.of(new File(workingDir, ScenarioLabels.amodeusFile), 0, 1);
        // DeleteDirectory.of(new File(workingDir, ScenarioLabels.avFile), 0, 1);
        // DeleteDirectory.of(new File(workingDir, ScenarioLabels.config), 0, 1);
        // DeleteDirectory.of(new File(workingDir, ScenarioLabels.pt2MatSettings), 0, 1);
        // DeleteDirectory.of(new File(workingDir, ScenarioLabels.network), 0, 1);
    }

    // ---

    private ChicagoScenarioCreation(Network network, MatsimAmodeusDatabase db, File taxiTripsFile, File directory) {
        super(network, db, taxiTripsFile, directory);
    }
}
