/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.sanfrancisco;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class GlobalRequestIndex {
    private HashMap<VehReqPair, Integer> reqMap = new HashMap<>();

    /** adds request for vehicle with index
     * 
     * @param vehicleIndex and its local request index
     * @param vehReqIndex
     * @return associated globalReqIndex */
    public Integer add(int vehicleIndex, int vehReqIndex) {
        VehReqPair vrp = new VehReqPair(vehicleIndex, vehReqIndex);
        return reqMap.computeIfAbsent(vrp, v -> reqMap.size() + 1);
    }

    // TODO check if actually needed
    public SortedSet<Integer> getGlobalIDs() {
        return reqMap.values().stream().sorted().collect(Collectors.toCollection(TreeSet::new));
    }
}
