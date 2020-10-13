package amodeus.amodtaxi.scenario.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import amodeus.amodeus.net.FastLinkLookup;
import amodeus.amodeus.util.math.SI;
import amodeus.amodtaxi.trace.TaxiStamp;
import amodeus.amodtaxi.trace.TaxiStampHelpers;
import amodeus.amodtaxi.util.CSVUtils;
import amodeus.amodtaxi.util.ReverseLineInputStream;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.qty.Quantity;
import org.matsim.core.router.util.LeastCostPathCalculator;

/* package */ class FileSummary extends Summary {
    public static Summary of(File file, TaxiStampReader stampReader, FastLinkLookup fastLinkLookup, LeastCostPathCalculator leastCostPathCalculator) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(file)))) {
            Collection<TaxiStamp> taxiStamps = reader.lines().map(line -> CSVUtils.csvLineToList(line, " ")).map(stampReader::read).collect(Collectors.toList());
            return new FileSummary(taxiStamps, file, fastLinkLookup, leastCostPathCalculator);
        }
    }

    // ---

    private final NavigableMap<LocalDate, Tensor> distances;
    private final NavigableMap<LocalDate, Tensor> journeyTimes;

    protected FileSummary(Collection<TaxiStamp> taxiStamps, File file, FastLinkLookup fastLinkLookup, LeastCostPathCalculator leastCostPathCalculator) {
        super(taxiStamps, Collections.singleton(file));
        distances = stampsByDay.entrySet().stream().collect(Collectors.toMap( //
                Map.Entry::getKey, //
                e -> {
                    NetworkDistanceHelper distanceHelper = new NetworkDistanceHelper(e.getValue(), fastLinkLookup, leastCostPathCalculator);
                    return Tensors.of(distanceHelper.getEmptyDistance(), distanceHelper.getCustomerDistance());
                }, //
                (v1, v2) -> { throw new RuntimeException(); }, //
                TreeMap::new));
        journeyTimes = stampsByDay.entrySet().stream().collect(Collectors.toMap( //
                Map.Entry::getKey, //
                e -> {
                    try {
                        return JourneyTimes.in(e.getValue());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw new RuntimeException();
                    }
                }, //
                (v1, v2) -> { throw new RuntimeException(); }, //
                TreeMap::new));
    }

    @Override // from Summary
    public long numberOfRequests() {
        return TaxiStampHelpers.numberOfRequests(stamps());
    }

    @Override // from Summary
    public Scalar emptyDistance() {
        return distances.values().stream().map(vector -> vector.Get(0)).reduce(Scalar::add).orElseThrow();
    }

    @Override // from Summary
    public Scalar customerDistance() {
        return distances.values().stream().map(vector -> vector.Get(1)).reduce(Scalar::add).orElseThrow();
    }

    @Override // from Summary
    protected Optional<Scalar> emptyDistance(LocalDate date) {
        return Optional.ofNullable(distances.get(date)).map(vector -> vector.Get(0));
    }

    @Override // from Summary
    protected Optional<Scalar> customerDistance(LocalDate date) {
        return Optional.ofNullable(distances.get(date)).map(vector -> vector.Get(1));
    }

    @Override // from Summary
    public Tensor journeyTimes() {
        return journeyTimes.values().stream().reduce(Join::of).orElse(Tensors.empty());
    }

    @Override // from Summary
    protected Tensor journeyTimes(LocalDate date) {
        return journeyTimes.getOrDefault(date, Tensors.empty());
    }

    @Override // from Summary
    public Summary on(LocalDate date) {
        return new Summary(stampsByDay.getOrDefault(date, Collections.emptyList()), sources()) {
            @Override
            public NavigableSet<LocalDate> dates() {
                return new TreeSet<>(Collections.singleton(date));
            }

            @Override
            public long numberOfRequests() {
                return TaxiStampHelpers.numberOfRequests(stamps());
            }

            @Override
            public Scalar emptyDistance() {
                return FileSummary.this.emptyDistance(date).orElse(Quantity.of(0, SI.METER));
            }

            @Override
            public Scalar customerDistance() {
                return FileSummary.this.customerDistance(date).orElse(Quantity.of(0, SI.METER));
            }

            @Override
            protected Optional<Scalar> emptyDistance(LocalDate date) {
                return Optional.of(emptyDistance());
            }

            @Override
            protected Optional<Scalar> customerDistance(LocalDate date) {
                return Optional.of(customerDistance());
            }

            @Override
            public Tensor journeyTimes() {
                return FileSummary.this.journeyTimes(date);
            }

            @Override
            protected Tensor journeyTimes(LocalDate date) {
                return journeyTimes();
            }

            @Override
            public Summary on(LocalDate date) {
                return FileSummary.this.on(date);
            }
        };
    }
}
