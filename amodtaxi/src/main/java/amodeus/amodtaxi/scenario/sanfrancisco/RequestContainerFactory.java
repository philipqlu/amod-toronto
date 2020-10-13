/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.sanfrancisco;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;

import amodeus.amodeus.dispatcher.core.RequestStatus;
import amodeus.amodeus.net.RequestContainer;
import amodeus.amodeus.util.AmodeusTimeConvert;
import amodeus.amodeus.util.math.GlobalAssert;

/* package */ class RequestContainerFactory {
    private final String requestIndex;
    private final int fromLinkIndex;
    private final int toLinkIndex;
    private final LocalDateTime submissionTime;
    private final AmodeusTimeConvert timeConvert;

    public RequestContainerFactory(String rIndex, int fromLIndex, int toLIndex, //
            LocalDateTime submissionTime, AmodeusTimeConvert timeConvert) {
        GlobalAssert.that(timeConvert.toEpochSec(submissionTime) >= 1211018404 - 4 - 3600 * 3);
        GlobalAssert.that(timeConvert.toEpochSec(submissionTime) <= 1213088836 + 24 * 3600);
        this.requestIndex = rIndex;
        this.fromLinkIndex = fromLIndex;
        this.toLinkIndex = toLIndex;
        this.submissionTime = submissionTime;
        this.timeConvert = timeConvert;
    }

    public RequestContainer create(RequestStatus status, LocalDate simulationDate) {
        RequestContainer requestContainer = new RequestContainer();
        requestContainer.requestIndex = Integer.parseInt(requestIndex);
        requestContainer.fromLinkIndex = fromLinkIndex;
        requestContainer.toLinkIndex = toLinkIndex;
        requestContainer.submissionTime = timeConvert.ldtToAmodeus(submissionTime, simulationDate);
        requestContainer.requestStatus = new HashSet<>(Collections.singletonList(status));
        return requestContainer;
    }
}
