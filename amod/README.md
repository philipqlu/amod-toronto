# amodeus.amod <a href="https://travis-ci.org/amodeus-science/amod"><img src="https://travis-ci.org/amodeus-science/amod.svg?branch=master" alt="Build Status"></a>

This repository allows to run an autonomous mobility-on-demand scenario using the [amodeus library](https://github.com/amodeus-science/amodeus).

Try it, orchestrate your own fleet of amod-taxis! Watch a [visualization](https://www.youtube.com/watch?v=QkFtIQQSHto) of a traffic simulation in San Francisco generated using this repository.

<table><tr>
<td>

![p1t1](https://user-images.githubusercontent.com/4012178/38852194-23c0b602-4219-11e8-90af-ce5c589ddf47.png)

<td>

![p1t4](https://user-images.githubusercontent.com/4012178/38852209-30616834-4219-11e8-81db-41fe71f7599e.png)

<td>

![p1t3](https://user-images.githubusercontent.com/4012178/38852252-4f4d178e-4219-11e8-9634-434200922ed0.png)

<td>

![p1t2](https://user-images.githubusercontent.com/4012178/38852212-3200c8d8-4219-11e8-9dad-eb0aa33e1357.png)

</tr></table>

## Admins

AMoDeus is jointly maintained and further developed by the Admins and Code Owners Christian Fluri (ETH Zürich), Joel Gächter (ETH Zürich), Sebastian Hörl (ETH  Zürich), Claudio Ruch, Jan Hakenberg, ChengQi Lu (TU Berlin), and Marc Albert (nuTonomy). 

Please let us know if you'd like to contribute!

## First steps in the amod repository

### Prerequisites

- You may work on a Linux, Mac or Windows OS with a set of different possible IDEs. The combination Ubuntu, Java 8, Eclipse has worked well. 
- Install Java SE Development Kit (version 8, or above)
- Install Apache Maven
- Install IDE (ideally Eclipse Oxygen or Photon)
- Install GLPK and GLPK for Java (Ensure you install compatible versions, e.g. [here](http://glpk-java.sourceforge.net/gettingStarted.html))
	- Prerequisites are: GCC, Libtool, Swig and Subversion
- Install Git and connect to GitHub with [SSH](https://help.github.com/articles/connecting-to-github-with-ssh/)

The code format of the `amod` repository is specified in the `amodeus` profile that you can import from [amodeus-code-style.xml](https://raw.githubusercontent.com/amodeus-science/amodeus/master/amodeus-code-style.xml).

### Getting Started

Follow these step-by-step instructions or the [video](https://www.youtube.com/watch?v=hay5VGhQ7S8) to set up, prepare, and run your first simulation. You can get a sample simulation scenario at https://www.amodeus.science/.
1. Clone this repository
2. Import to eclipse as existing maven project (Package Explorer->Import) using the pom.xml in the top folder of this repository.
3. Set up Run Configuration for `ScenarioCreator`: set the Working Directory to
   be an empty folder in your workspace. Pass in the city name as the argument
   (eg. `toronto`).
4. Set up Run Configurations for `ScenarioPreparer`, `ScenarioServer`, `ScenarioViewer`, choose the Working Directory to be the `CreatedScenarios`
   folder inside where you created the scenario.
6. Adjust the simulation settings in the 2 config files:
   AmodeusOptions.properties for AMoDeus settings (e.g. max number of people)
   and config_full.xml for Matsim settings (e.g. output directory, policy, fleet size)
7. Add JAVA VM arguments if necessary, e.g., `-Xmx10000m` to run with 10 GB of RAM and `-Dmatsim.preferLocalDtds=true` to prefer the local Dtds. 
8. Run the `ScenarioPreparer` as a Java application: wait until termination
9. Run the `ScenarioServer` as a Java application: the simulation should run
10. Run the `ScenarioViewer` as a Java application: the visualization of the scenario should open in a separate window

## Gallery

<table><tr>
<td>

![usecase_amodeus](https://user-images.githubusercontent.com/4012178/35968174-668b6e54-0cc3-11e8-9c1b-a3e011fa0600.png)

Zurich

<td>

![p1t5](https://user-images.githubusercontent.com/4012178/38852351-ce176dc6-4219-11e8-93a5-7ad58247e82b.png)

San Francisco

<td>

![San Francisco](https://user-images.githubusercontent.com/4012178/37365948-4ab45794-26ff-11e8-8e2d-ceb1b526e962.png)

San Francisco

</tr></table>

![ethz300](https://user-images.githubusercontent.com/4012178/45925071-bf9d3b00-bf0e-11e8-9d92-e30650fd6bf6.png)
