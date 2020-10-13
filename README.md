# amod-toronto
Autonomous Mobility-on-Demand (AMoD) simulation and analysis pipeline for
Toronto.

## Installation
* To use data_utils:
```
cd data_utils
pip install -r requirements.txt
```
* You will need `GDAL` for the postprocessing notebook. Install by going to
  https://pypi.org/project/GDAL/ or running `conda install GDAL`.
* To configure AMoDeus, follow the `README.md` in
[amod](https://github.com/philipqlu/amod-toronto/amod) and 
[amodtaxi](https://github.com/philipqlu/amod-toronto/amodtaxi).

## amod
Contains code to run the scenario creation and simulation pipeline. Uses 
**amodtaxi** to create the initial scenario.

### How to run in Eclipse
* Create a folder to store the simulation results.
* Set up run configurations, setting the working directory to the created folder
above. See [amod](https://github.com/philipqlu/amod-toronto/amod) for
more instructions.

## amodtaxi
Contains code and configuration for creating the MATSim/AMoDeus scenario for
Toronto.

To create your own scenario, at minimum you require:
* population (eg. taxi trips dataset)
* location specs consisting of the bounding box, EPSG code, and reference frame
center for the area of interest

The scenario settings are stored in the `src/main/resources/` folder. The
rest is a data pipeline that runs these steps: loading the map and trip dataset, 
building a MATSim network, and creating a population.

## data_utils
Contains preprocessing pipeline for synthesizing a population/trip dataset and
postprocessing code for cleaning and plotting the results.

## Demo
### Model-Free Adaptive Repositioning
![Model-free adaptive repositioning with fleet size = 100](images/mf_fs_100.gif)

### Global Euclidean Bipartite Matching
![GBM with fleet size = 100](images/gbm_fs_100.gif)
