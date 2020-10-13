/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.sanfrancisco;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Objects;

import amodeus.amodeus.net.FastLinkLookup;
import amodeus.amodeus.net.SimulationObject;
import amodeus.amodeus.net.StorageSubscriber;
import amodeus.amodeus.net.StorageUtils;
import amodeus.amodeus.util.AmodeusTimeConvert;
import amodeus.amodeus.util.LocalDateTimes;
import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodtaxi.trace.DayTaxiRecord;
import amodeus.amodtaxi.trace.TaxiStamp;
import amodeus.amodtaxi.trace.TaxiTrail;
import ch.ethz.idsc.tensor.Scalar;

public class SimulationFleetDumperSF {
    private final Scalar tStepSize;
    private final AmodeusTimeConvert timeConvert;
    private final SimContainerPopulator populator;
    // private Set<List<Pair<Integer, RequestStatus>>> requestTrails = new HashSet<>();
    // private LinkedHashSet<Pair<Integer, Integer>> requestMap = new LinkedHashSet<>();

    public SimulationFleetDumperSF(FastLinkLookup fastLinkLookup, Scalar tStepSize, AmodeusTimeConvert timeConvert) {
        this.tStepSize = tStepSize;
        this.timeConvert = timeConvert;
        this.populator = new SimContainerPopulator(fastLinkLookup, timeConvert);
    }

    public void createDumpOf(DayTaxiRecord dayTaxiRecord, File outputFolder, LocalDate simulationDate) throws IOException {
        System.out.println("INFO number of dayTaxiRecords: " + dayTaxiRecord.numTaxis());
        System.out.println("outputfolder for storage utils: " + outputFolder.getAbsolutePath());

        StorageUtils storageUtils = new StorageUtils(outputFolder);

        int totalMatchedRequests = 0;
        GlobalRequestIndex globalReqIndex = new GlobalRequestIndex();

        LocalDateTime maxTime = timeConvert.endOf(simulationDate);
        for (LocalDateTime glblTime = timeConvert.beginOf(simulationDate); glblTime.isEqual(maxTime) || glblTime.isBefore(maxTime); //
                glblTime = LocalDateTimes.addTo(glblTime, tStepSize)) {

            // if (glblTime % 10000 == 0)
            // System.out.println("INFO processing timestep = " + glblTime + "\r");

            SimulationObject simulationObject = new SimulationObject();
            simulationObject.now = timeConvert.ldtToAmodeus(glblTime, simulationDate);
            simulationObject.vehicles = new ArrayList<>();
            simulationObject.total_matchedRequests = totalMatchedRequests;

            /** VehicleContainer and RequestContainer **/
            for (int vehicleIndex = 0; vehicleIndex < dayTaxiRecord.numTaxis(); ++vehicleIndex) {
                TaxiTrail taxiTrail = dayTaxiRecord.get(vehicleIndex);

                Entry<LocalDateTime, TaxiStamp> taxiStampEntry = taxiTrail.getTaxiStamps().floorEntry(glblTime);

                if (Objects.nonNull(taxiStampEntry)) {
                    GlobalAssert.that(taxiTrail instanceof TaxiTrailSF);
                    TaxiTrailSF taxiTrailSF = (TaxiTrailSF) taxiTrail;

                    populator.with(taxiStampEntry.getValue(), vehicleIndex, //
                            simulationObject, //
                            globalReqIndex, simulationDate, taxiTrailSF.getRequestInseter());
                } else {
                    populator.withEmpty(simulationObject, vehicleIndex);
                }
            }
            totalMatchedRequests = simulationObject.total_matchedRequests;

            new StorageSubscriber(storageUtils).handle(simulationObject);
        }

        File superFolder = outputFolder.getParentFile().getParentFile().getParentFile();

        File file = new File(superFolder + "/info" + simulationDate.toString());
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
            for (int vehicleIndex = 0; vehicleIndex < dayTaxiRecord.numTaxis(); ++vehicleIndex) {
                out.write("=====\n");
                out.write("Entries in simulationObject part: \n");
                out.write("File name: " + dayTaxiRecord.get(vehicleIndex).getID() + "\n");
                out.write("LocalDate: " + simulationDate + "\n");
                out.write("=====\n");
            }
        }

        System.out.println("totalMatchedRequests =" + totalMatchedRequests);

        System.out.println("INFO total total matched requests: " + totalMatchedRequests);
        // TODO see why this happens.
        // GlobalAssert.that(dropped == 0);
        // TODO see why requestIndex are not equal to the totalMatchedRequest
        // GlobalAssert.that(requestIndex == totalMatchedRequests);
        // FleetLogUtils.writeRequestMap(outputFolder, requestMap);
        // FleetLogUtils.countAllRequests(requestTrails);
        // FleetLogUtils.writeRequestSummary(new File(outputFolder + "/RequestSummary.txt"), requestTrails);
    }
}