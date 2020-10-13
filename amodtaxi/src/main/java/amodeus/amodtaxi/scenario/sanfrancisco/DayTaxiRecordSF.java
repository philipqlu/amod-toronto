/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.sanfrancisco;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amodeus.amodeus.net.FastLinkLookup;
import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodtaxi.trace.DayTaxiRecord;
import amodeus.amodtaxi.trace.TaxiTrail;

/* package */ class DayTaxiRecordSF implements DayTaxiRecord {
    private final Map<Integer, TaxiTrail> trails = new HashMap<>();
    private final FastLinkLookup fastLinkLookup;

    public DayTaxiRecordSF(FastLinkLookup fastLinkLookup) {
        this.fastLinkLookup = fastLinkLookup;
    }

    @Override
    public void insert(List<String> list, int taxiStampID, String id) {
        trails.computeIfAbsent(taxiStampID, i -> new TaxiTrailSF(id, fastLinkLookup)).insert(list);
    }

    @Override
    public void processFilledTrails() throws Exception {
        GlobalAssert.that(trails.size() > 0);
        int requestIndex = 1;
        for (TaxiTrail taxiTrail : trails.values()) {
            taxiTrail.processFilledTrail();

            // GlobalAssert.that(taxiTrail instanceof TaxiTrailSF);
            // TaxiTrailSF taxiTrailSF = (TaxiTrailSF) taxiTrail;
            // int requestIndexUsed = taxiTrailSF.setRequestContainers(requestIndex, db, fastLinkLookup);
            // requestIndex = requestIndexUsed + 1;
            // GlobalAssert.that(requestIndex >= 0);
        }
    }

    // /** this should only be run after initialization, for every time step, it
    // * creates a RequestContainer */
    // public void processRoboTaxiStatus() {
    // GlobalAssert.that(trails.size() > 0);
    // int requestIndex = 1;
    // for (TaxiTrailInterface taxiTrail : trails.values()) {
    // GlobalAssert.that(taxiTrail instanceof TaxiTrailSF);
    // TaxiTrailSF taxiTrailSF = (TaxiTrailSF) taxiTrail;
    // taxiTrailSF.setRoboTaxiStatus();
    // }
    //
    // }

    @Override
    public LocalDateTime getMaxTime() {
        return trails.values().stream().map(TaxiTrail::getMaxTime).max(LocalDateTime::compareTo).get();
    }

    @Override
    public int numTaxis() {
        return trails.size();
    }

    @Override
    public Collection<TaxiTrail> getTrails() {
        return trails.values();
    }

    @Override
    public TaxiTrail get(int vehicleIndex) {
        return trails.get(vehicleIndex);
    }
}
