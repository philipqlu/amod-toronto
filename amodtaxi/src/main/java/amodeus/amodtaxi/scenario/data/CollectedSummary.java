package amodeus.amodtaxi.scenario.data;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;

/* package */ class CollectedSummary extends Summary {
    private static final String BREAK = "\n==========================================\n";

    private final Collection<Summary> summaries;

    protected CollectedSummary(Collection<Summary> summaries) {
        super(summaries.stream().map(Summary::stamps).flatMap(Collection::stream).collect(Collectors.toList()), //
                summaries.stream().map(Summary::sources).flatMap(Collection::stream).collect(Collectors.toSet()));
        this.summaries = summaries;
    }

    @Override
    public long numberOfRequests() {
        return summaries.stream().mapToLong(Summary::numberOfRequests).sum();
    }

    @Override // from Summary
    public Scalar emptyDistance() {
        return summaries.stream().map(Summary::emptyDistance).reduce(Scalar::add).orElseThrow();
    }

    @Override // from Summary
    public Scalar customerDistance() {
        return summaries.stream().map(Summary::customerDistance).reduce(Scalar::add).orElseThrow();
    }

    @Override // from Summary
    protected Optional<Scalar> emptyDistance(LocalDate date) {
        return summaries.stream().map(summary -> summary.emptyDistance(date)).filter(Optional::isPresent).map(Optional::get).reduce(Scalar::add);
    }

    @Override // from Summary
    protected Optional<Scalar> customerDistance(LocalDate date) {
        return summaries.stream().map(summary -> summary.customerDistance(date)).filter(Optional::isPresent).map(Optional::get).reduce(Scalar::add);
    }

    @Override // from Summary
    public Tensor journeyTimes() {
        return summaries.stream().map(Summary::journeyTimes).reduce(Join::of).orElse(Tensors.empty());
    }

    @Override // from Summary
    protected Tensor journeyTimes(LocalDate date) {
        return summaries.stream().map(summary -> summary.journeyTimes(date)).reduce(Join::of).orElse(Tensors.empty());
    }

    @Override // from Summary
    public Summary on(LocalDate date) {
        return new CollectedSummary(summaries.stream().map(summary -> summary.on(date)).collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return super.toString() + BREAK + summaries.stream().map(Summary::toString).collect(Collectors.joining(BREAK));
    }
}
