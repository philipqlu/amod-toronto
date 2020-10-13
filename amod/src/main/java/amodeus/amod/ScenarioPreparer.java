/* amod - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amod;

import java.io.File;
import java.net.MalformedURLException;

import amodeus.amodeus.options.ScenarioOptions;
import amodeus.amodeus.options.ScenarioOptionsBase;
import amodeus.amodeus.prep.ConfigCreator;
import amodeus.amodeus.prep.NetworkPreparer;
import amodeus.amodeus.prep.PopulationPreparer;
import amodeus.amodeus.prep.TheApocalypse;
import amodeus.amodeus.prep.VirtualNetworkPreparer;
import amodeus.amodeus.util.io.MultiFileTools;
import org.matsim.amodeus.config.AmodeusConfigGroup;
import org.matsim.amodeus.config.modal.GeneratorConfig;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

import amodeus.amod.ext.Static;

/** Class to prepare a given scenario for MATSim, includes preparation of
 * network, population, creation of virtualNetwork and travelData objects. As an
 * example a user may want to restrict the population size to few 100s of agents
 * to run simulations quickly during testing, or the network should be reduced
 * to a certain area. */
/* package */ enum ScenarioPreparer {
    ;

    public static void main(String[] args) throws MalformedURLException, Exception {
        File workingDirectory = MultiFileTools.getDefaultWorkingDirectory();
        run(workingDirectory);
    }

    /** loads scenario preparer in the {@link File} workingDirectory @param
     * workingDirectory
     * 
     * @throws MalformedURLException
     * @throws Exception */
    public static void run(File workingDirectory) throws MalformedURLException, Exception {
        Static.setup();
        System.out.println("\n\n\n" + Static.glpInfo() + "\n\n\n");

        /** The {@link ScenarioOptions} loads 2 options files:
         * MATSim/AV configurations (config.xml) and
		 * AMoDeus configurations (AmodeusOptions.properties).
         *  */
        ScenarioOptions scenarioOptions = new ScenarioOptions(workingDirectory, ScenarioOptionsBase.getDefault());
        Static.setLPtoNone(workingDirectory);

        /** MATSim config */
        AmodeusConfigGroup avConfigGroup = new AmodeusConfigGroup();
        Config config = ConfigUtils.loadConfig(scenarioOptions.getPreparerConfigName(), avConfigGroup);
        Scenario scenario = ScenarioUtils.loadScenario(config);
        GeneratorConfig genConfig = avConfigGroup.getModes().values().iterator().next().getGeneratorConfig();
        int numRt = genConfig.getNumberOfVehicles();
        System.out.println("NumberOfVehicles=" + numRt);

        /** adaption of MATSim network, e.g., radius cutting */
        Network network = scenario.getNetwork();
        network = NetworkPreparer.run(network, scenarioOptions);

        /** adaption of MATSim population, e.g., radius cutting */
        Population population = scenario.getPopulation();

        // Population cutting
        TheApocalypse.reducesThe(population).toNoMoreThan(scenarioOptions.getMaxPopulationSize());
        long apoSeed = 1234;
        PopulationPreparer.run(network, population, scenarioOptions, config, apoSeed);

        /** creating a virtual network, e.g., for operational policies requiring a graph
         * structure on the city */
        int endTime = (int) config.qsim().getEndTime().seconds();
        VirtualNetworkPreparer.INSTANCE.create(network, population, scenarioOptions, numRt, endTime); //

        /** create a simulation MATSim config file linking the created input data */
        ConfigCreator.createSimulationConfigFile(config, scenarioOptions);
    }
}
