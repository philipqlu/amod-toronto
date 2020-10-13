/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.sanfrancisco;

import java.time.LocalDate;
import java.util.Collection;

import amodeus.amodeus.net.FastLinkLookup;
import amodeus.amodeus.taxitrip.PersonCreate;
import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodeus.util.AmodeusTimeConvert;
import amodeus.amodeus.util.math.GlobalAssert;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;

@Deprecated
/* package */ class Populator {
    private long globalId = 0;
    private final Population population;
    private final AmodeusTimeConvert timeConvert;
    private final FastLinkLookup fastLinkLookup;
    private final PopulationFactory populationFactory;
    private final LocalDate simulationDate;

    public Populator(Population population, AmodeusTimeConvert timeConvert, //
            FastLinkLookup fastLinkLookup, LocalDate simulationDate) {//
        this.population = population;
        this.timeConvert = timeConvert;
        this.fastLinkLookup = fastLinkLookup;
        populationFactory = population.getFactory();
        this.simulationDate = simulationDate;
    }

    public void convert(Collection<TaxiTrip> taxiTrips) {
        /** insert all {@link TaxiTrip}s into {@link Population} */
        taxiTrips.forEach(taxiTrip -> {
                    Person person = PersonCreate.fromTrip(taxiTrip, Integer.toString((int) (++globalId)), //
                            populationFactory, //
                            fastLinkLookup, simulationDate, timeConvert);
                    population.addPerson(person);
                });
        GlobalAssert.that(population.getPersons().size() == taxiTrips.size());
    }
}
