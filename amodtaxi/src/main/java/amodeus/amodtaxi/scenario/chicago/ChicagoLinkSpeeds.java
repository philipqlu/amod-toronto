/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.chicago;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import amodeus.amodeus.linkspeed.LinkSpeedUtils;
import amodeus.amodeus.net.MatsimAmodeusDatabase;
import amodeus.amodeus.options.ScenarioOptions;
import amodeus.amodeus.options.ScenarioOptionsBase;
import amodeus.amodeus.taxitrip.ImportTaxiTrips;
import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodeus.util.matsim.NetworkLoader;
import amodeus.amodtaxi.scenario.ScenarioLabels;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

import amodeus.amodtaxi.linkspeed.TaxiLinkSpeedEstimator;
import amodeus.amodtaxi.linkspeed.batch.FlowTimeInvLinkSpeed;
import amodeus.amodtaxi.linkspeed.batch.GLPKLinOptDelayCalculator;

/* package */ enum ChicagoLinkSpeeds {
    ;

    public static void compute(File processingDir, File finalTripsFile) throws Exception {
        File linkSpeedsFile = new File(processingDir, ScenarioLabels.linkSpeedData);

        // load necessary files
        ScenarioOptions scenarioOptions = new ScenarioOptions(processingDir, //
                ScenarioOptionsBase.getDefault());
        File configFile = new File(scenarioOptions.getPreparerConfigName());
        System.out.println(configFile.getAbsolutePath());
        GlobalAssert.that(configFile.exists());
        Config configFull = ConfigUtils.loadConfig(configFile.toString());
        Network network = NetworkLoader.fromNetworkFile(new File(processingDir, configFull.network().getInputFile()));
        MatsimAmodeusDatabase db = MatsimAmodeusDatabase.initialize(network, scenarioOptions.getLocationSpec().referenceFrame());

        // import final trips
        Collection<TaxiTrip> trips = new ArrayList<>(ImportTaxiTrips.fromFile(finalTripsFile));

        // export link speed estimation
        // QuadTree<Link> qt = FastQuadTree.of(network);
        TaxiLinkSpeedEstimator lsCalc = new FlowTimeInvLinkSpeed(trips, network, db, GLPKLinOptDelayCalculator.INSTANCE);
        LinkSpeedUtils.writeLinkSpeedData(linkSpeedsFile, lsCalc.getLsData());
    }
}
