/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.sanfrancisco;

import java.util.Objects;

/* package */ class VehReqPair {
    private final int vehicleIndex;
    private final int localReqIndex;

    /* package */ VehReqPair(int vehicleIndex, int localReqIndex) {
        this.vehicleIndex = vehicleIndex;
        this.localReqIndex = localReqIndex;
    }

    public int getVind() {
        return vehicleIndex;
    }

    public int getRind() {
        return localReqIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VehReqPair))
            return false;
        VehReqPair vrp = (VehReqPair) o;
        return vrp.getVind() == this.vehicleIndex && //
                vrp.getRind() == this.localReqIndex;

    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleIndex, localReqIndex);
    }

}
