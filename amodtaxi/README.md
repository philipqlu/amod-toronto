# amodeus.amodtaxi <a href="https://travis-ci.org/amodeus-science/amodtaxi"><img src="https://travis-ci.org/amodeus-science/amodtaxi.svg?branch=master" alt="Build Status"></a>

This repository enables you to crete your own scenarios to be used in [amodeus](https://github.com/amodeus-science/amodeus).

## Admins

AMoDeus is jointly maintained and further developed by the Admins Christian Fluri (ETH Zürich), Joel Gächter (ETH Zürich), Sebastian Hörl (ETH  Zürich), Claudio Ruch, Jan Hakenberg, ChengQi Lu (TU Berlin), and Marc Albert (nuTonomy).

Please let us know if you'd like to contribute!

## Purpose

This repository allows to generate the files necessary to run [amodeus](https://github.com/amodeus-science/amodeus) simulations as shown in the [amod](https://github.com/amodeus-science/amod) repository, from real data.

The locations provided are:
- **Chicago** using [ChicagoScenario](https://github.com/amodeus-science/amodtaxi/blob/master/src/main/java/ch/ethz/idsc/amodtaxi/scenario/chicago/ChicagoScenarioCreation.java)
    - downloads data on its own through API from [City of Chicago](https://data.cityofchicago.org/Transportation/Taxi-Trips/wrvz-psew)
- **San Francisco** using [SanFranciscoScenarioCreation](https://github.com/amodeus-science/amodtaxi/blob/master/src/main/java/ch/ethz/idsc/amodtaxi/scenario/sanfrancisco/SanFranciscoScenarioCreation.java)
    - uses a mini-slice of the dataset of mobility traces of taxi cabs in San Francisco, USA, by Michal Piorkowski, Natasa Sarafijanovic-Djukic, and Matthias Grossglauser (full set available at https://crawdad.org/epfl/mobility/20090224/)

Our website is [amodeus.science](https://www.amodeus.science/).