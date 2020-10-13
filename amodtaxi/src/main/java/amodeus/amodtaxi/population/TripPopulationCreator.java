/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.population;

import java.io.File;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import amodeus.amodeus.net.FastLinkLookup;
import amodeus.amodeus.taxitrip.ExportTaxiTrips;
import amodeus.amodeus.taxitrip.ImportTaxiTrips;
import amodeus.amodeus.taxitrip.PersonCreate;
import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodeus.util.AmodeusTimeConvert;
import amodeus.amodeus.util.io.GZHandler;
import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodtaxi.scenario.ScenarioLabels;
import amodeus.amodtaxi.util.NamingConvention;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.core.config.Config;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.io.PopulationWriter;

import amodeus.amodtaxi.tripfilter.TaxiTripFilterCollection;

public class TripPopulationCreator {
    private final FastLinkLookup fastLinkLookup;
    private final LocalDate simulationDate;
    private final AmodeusTimeConvert timeConvert;
    private final Config config;
    private final Network network;
    private final File populationFile;
    private final File populationFileGz;
    private final TaxiTripFilterCollection finalFilters;
    private final TaxiDistanceCalculator distCalc;
    private File finalTripFile;

    public TripPopulationCreator(File processingDir, Config config, Network network, FastLinkLookup fastLinkLookup, //
            LocalDate simualtionDate, AmodeusTimeConvert timeConvert, TaxiTripFilterCollection finalFilters) {
        this.fastLinkLookup = fastLinkLookup;
        this.simulationDate = simualtionDate;
        this.timeConvert = timeConvert;
        this.config = config;
        this.network = network;
        this.finalFilters = finalFilters;
        this.distCalc = new TaxiDistanceCalculator(processingDir, network, fastLinkLookup);
        populationFile = new File(processingDir, ScenarioLabels.population);
        populationFileGz = new File(processingDir, ScenarioLabels.populationGz);
    }

    public void process(File inFile) throws MalformedURLException, Exception {
        System.out.println("INFO start population creation");
        GlobalAssert.that(inFile.isFile());

        // Population creation (iterate trough all id's)
        System.out.println("Reading inFile:");
        System.out.println(inFile.getAbsolutePath());
        List<TaxiTrip> trips = ImportTaxiTrips.fromFile(inFile);

        File finalTripFile = new File(inFile.getParentFile(), NamingConvention.similarTo(inFile).apply("final"));
        process(trips, finalTripFile);
    }

    public void process(List<TaxiTrip> trips, File finalTripFile) throws MalformedURLException, Exception {
        this.finalTripFile = finalTripFile;

        // Population init
        Population population = PopulationUtils.createPopulation(config, network);
        PopulationFactory populationFactory = population.getFactory();

        // filter the stream
        List<TaxiTrip> finalFilteredTrips = new ArrayList<>();
        Stream<TaxiTrip> filtered = finalFilters.filterStream(trips.stream());

        // create persons
        filtered.forEach(taxiTrip -> {
            Person person = PersonCreate.fromTrip(taxiTrip, taxiTrip.localId, populationFactory, //
                    fastLinkLookup, simulationDate, timeConvert);
            population.addPerson(person);
            distCalc.addTrip(taxiTrip);
            finalFilteredTrips.add(taxiTrip);
        });

        // export finally used set of trips
        ExportTaxiTrips.toFile(finalFilteredTrips.stream(), finalTripFile);

        // write the modified population to file
        System.out.println("Population size: " + population.getPersons().size());
        PopulationWriter populationWriter = new PopulationWriter(population);
        populationWriter.write(populationFileGz.toString());

        // extract the created .gz file
        GZHandler.extract(populationFileGz, populationFile);

        System.out.println("PopulationSize: " + population.getPersons().size());
        if (population.getPersons().size() > 0)
            System.out.println("INFO successfully created population");
        else
            System.err.println("WARN created population is empty");

        // export taxi trip distance analysis
        distCalc.exportTotalDistance();
        finalFilters.printSummary();
    }

    public File getFinalTripFile() {
        Objects.requireNonNull(finalTripFile);
        return finalTripFile;
    }
}
