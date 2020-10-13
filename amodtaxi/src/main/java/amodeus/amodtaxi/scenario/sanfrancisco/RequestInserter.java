/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.sanfrancisco;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import amodeus.amodeus.dispatcher.core.RequestStatus;
import amodeus.amodeus.net.FastLinkLookup;
import amodeus.amodeus.net.RequestContainer;
import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodeus.util.AmodeusTimeConvert;
import amodeus.amodeus.util.math.GlobalAssert;
import amodeus.amodtaxi.trace.TaxiStamp;

/* package */ class RequestInserter {
    private final AmodeusTimeConvert timeConvert;
    private final FastLinkLookup fastLinkLookup;
    // private final String taxiId;
    private Map<TaxiStamp, Collection<RequestContainer>> reqContainers = new HashMap<>();

    public RequestInserter(AmodeusTimeConvert timeConvert, FastLinkLookup fastLinkLookup, String taxiId) {
        this.timeConvert = timeConvert;
        this.fastLinkLookup = fastLinkLookup;
        // TODO taxi id is not used
        // this.taxiId = taxiId;
    }

    /** Function computes all required {@link RequestContainer}s from the inserted
     * map @param timeTaxiStamps. Every request belongs to the date of its submission, even
     * if its arrival is on the next day! */
    public void insert(NavigableMap<LocalDateTime, TaxiStamp> timeTaxiStamps, Collection<TaxiTrip> taxiTrips) {
        // Collection<TaxiTrip> taxiTrips = TaxiTripFinder.in(timeTaxiStamps, taxiId);
        System.err.println("found " + taxiTrips.size() + " taxi trips.");

        for (TaxiTrip taxiTrip : taxiTrips) {
            Map<RequestStatus, LocalDateTime> reqTimes = StaticHelper.getRequestTimes(taxiTrip.pickupTimeDate, timeTaxiStamps);

            /** basic setup of RequestContainer */
            LocalDateTime submissionTime = Stream.of(RequestStatus.REQUESTED, RequestStatus.ASSIGNED, RequestStatus.PICKUPDRIVE, RequestStatus.PICKUP, RequestStatus.DRIVING) //
                    .map(reqTimes::get).filter(Objects::nonNull).findFirst().get();
            LocalDate submissionDay = Objects.requireNonNull(submissionTime.toLocalDate());

            /** from link */
            int fromLinkIndex = fastLinkLookup.indexFromWGS84(timeTaxiStamps.get(taxiTrip.pickupTimeDate).gps);

            /** to link */
            LocalDateTime lastDriveTimeSTep = timeTaxiStamps.lowerKey(taxiTrip.dropoffTimeDate);
            int toLinkIndex = fastLinkLookup.indexFromWGS84(timeTaxiStamps.get(lastDriveTimeSTep).gps);

            RequestContainerFactory requestContainerFactory = new RequestContainerFactory( //
                    taxiTrip.localId, fromLinkIndex, toLinkIndex, //
                    submissionTime, timeConvert);

            /** add request containers before driving */
            for (RequestStatus status : new RequestStatus[]{ RequestStatus.REQUESTED, RequestStatus.ASSIGNED, RequestStatus.PICKUPDRIVE, RequestStatus.PICKUP })
                if (Objects.nonNull(reqTimes.get(status))) {
                    RequestContainer container = requestContainerFactory.create(status, submissionDay);
                    addContainer(timeTaxiStamps.get(reqTimes.get(status)), container);
                }

            /** add request containers while driving */
            LocalDateTime time = taxiTrip.pickupTimeDate;
            do {
                RequestContainer container = requestContainerFactory.create(RequestStatus.DRIVING, submissionDay);
                addContainer(timeTaxiStamps.get(time), container);
                time = timeTaxiStamps.higherKey(time);
            } while (Objects.nonNull(time) && (time.isEqual(taxiTrip.dropoffTimeDate) || time.isBefore(taxiTrip.dropoffTimeDate)));

            /** add request containers after driving */
            RequestContainer container = requestContainerFactory.create(RequestStatus.DROPOFF, submissionDay);
            addContainer(timeTaxiStamps.get(taxiTrip.dropoffTimeDate), container);
        }

        /** One {@link TaxiTrip} will produce several {@link RequestContainer} that are
         * saved in a {@link SimulationObject}, therefore if the below condition is
         * not met there must be a problem in the computation. */
        GlobalAssert.that(reqContainers.size() >= taxiTrips.size());
    }

    private void addContainer(TaxiStamp stamp, RequestContainer container) {
        reqContainers.computeIfAbsent(stamp, s -> new ArrayList<>()).add(container);
    }

    public Collection<RequestContainer> getReqContainerCopy(TaxiStamp taxiStamp) {
        Collection<RequestContainer> reqContainer = reqContainers.getOrDefault(taxiStamp, new ArrayList<>());
        return reqContainer.stream().map(RequestInserter::copy).collect(Collectors.toList());
    }

    private static RequestContainer copy(RequestContainer requestContainer) {
        RequestContainer copy = new RequestContainer();
        copy.fromLinkIndex = requestContainer.fromLinkIndex;
        copy.toLinkIndex = requestContainer.toLinkIndex;
        copy.submissionTime = requestContainer.submissionTime;
        copy.requestIndex = requestContainer.requestIndex;
        copy.requestStatus = requestContainer.requestStatus;
        return copy;
    }
}
