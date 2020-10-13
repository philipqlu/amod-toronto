package amodeus.amodtaxi.scenario;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import amodeus.amodeus.net.MatsimAmodeusDatabase;
import amodeus.amodeus.taxitrip.ImportTaxiTrips;
import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodtaxi.linkspeed.iterative.IterativeLinkSpeedEstimator;
import org.matsim.api.core.v01.network.Network;

public abstract class ScenarioCreation {
    private final File taxiTripsFile;
    private final File directory;
    protected final Network network;
    protected final MatsimAmodeusDatabase db;

    protected ScenarioCreation(Network network, MatsimAmodeusDatabase db, File taxiTripsFile, File directory) {
        this.network = network;
        this.db = db;
        this.taxiTripsFile = taxiTripsFile;
        this.directory = directory;
    }

    public File linkSpeedData(int iterations) throws IOException {
        return linkSpeedData(iterations, directory);
    }

    public File linkSpeedData(int iterations, File directory) throws IOException {
        new IterativeLinkSpeedEstimator(iterations, new Random()).compute(directory, network, db, taxiTrips());
        return new File(directory, ScenarioLabels.linkSpeedData);
    }

    public File taxiTripsFile() {
        return taxiTripsFile;
    }

    public List<TaxiTrip> taxiTrips() throws IOException  {
        return ImportTaxiTrips.fromFile(taxiTripsFile);
    }

    public File directory() {
        return directory;
    }
}
