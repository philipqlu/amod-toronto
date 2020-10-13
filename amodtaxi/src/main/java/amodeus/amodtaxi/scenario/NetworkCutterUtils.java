/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario;

import java.util.Objects;

import amodeus.amodeus.util.network.LinkModes;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;


public enum NetworkCutterUtils {
    ;

    public static Network modeFilter(Network originalNetwork, LinkModes modes) {
        if (modes.allModesAllowed) {
            System.out.println("No modes filtered. Network was not modified");
            return originalNetwork;
        }
        for (Link link : originalNetwork.getLinks().values()) {
            Node filteredFromNode = originalNetwork.getNodes().get(link.getFromNode().getId());
            Node filteredToNode = originalNetwork.getNodes().get(link.getToNode().getId());
            if (Objects.nonNull(filteredFromNode) && Objects.nonNull(filteredToNode)) {
                boolean allowedMode = modes.getModesSet().stream().anyMatch(link.getAllowedModes()::contains);
                if (!allowedMode) {
                    originalNetwork.removeLink(link.getId());
                }
            }

        }

        String output = modes.getModesSet().toString();
        System.out.println("The following modes are kept in the network: " + output);

        return originalNetwork;
    }
}
