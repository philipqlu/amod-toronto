/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.sanfrancisco;

import java.io.File;
import java.time.LocalDate;
import java.util.Collection;

import amodeus.amodeus.net.FastLinkLookup;
import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodeus.util.AmodeusTimeConvert;
import amodeus.amodeus.util.io.GZHandler;
import amodeus.amodtaxi.scenario.Consistency;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.groups.PlansConfigGroup;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.io.PopulationWriter;

@Deprecated
/* package */ enum AdamAndEve {
    ;

    public static void create(File workingDirectory, Collection<TaxiTrip> trips, Network network, FastLinkLookup fastLinkLookup, //
            AmodeusTimeConvert timeConvert, LocalDate simulationDate, String nameAdd) throws Exception {
        /** create a new {@link Population} */
        Population population = PopulationUtils.createPopulation(new PlansConfigGroup(), network);

        /** fill with {@link TaxiTrip}s */
        (new Populator(population, timeConvert, fastLinkLookup, simulationDate)).convert(trips);
        Consistency.check(population);

        /** write created {@link Population} to file */
        File populationFile = new File(workingDirectory, "population" + nameAdd + ".xml");
        File populationGzFile = new File(workingDirectory, "population" + nameAdd + ".xml.gz");
        System.out.println("INFO writing new population to:");
        System.out.println(populationFile.getPath());
        System.out.println(populationGzFile.getPath());
        PopulationWriter pw = new PopulationWriter(population);
        pw.write(populationGzFile.toString());

        /** extract .gz file */
        GZHandler.extract(populationGzFile, populationFile);
        System.out.println("INFO successfully created population");
    }
}
