package amodeus.amodtaxi.scenario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

import amodeus.amodtaxi.trace.TaxiStamp;
import amodeus.amodtaxi.trace.TaxiStampHelpers;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.matsim.api.core.v01.Coord;

public class TaxiStampHelpersTest {
    private final List<TaxiStamp> taxiStamps = new ArrayList<>();

    @Before
    public void setup() {
        {
            TaxiStamp taxiStamp = new TaxiStamp();
            taxiStamp.globalTime = LocalDateTime.of(2000, 1, 1, 10, 0, 0);
            taxiStamp.gps = new Coord(1, 3);
            taxiStamp.occupied = true;
            taxiStamps.add(taxiStamp);
        }
        {
            TaxiStamp taxiStamp = new TaxiStamp();
            taxiStamp.globalTime = LocalDateTime.of(2000, 1, 1, 9, 0, 0);
            taxiStamp.gps = new Coord(3, 2);
            taxiStamps.add(taxiStamp);
        }
        {
            TaxiStamp taxiStamp = new TaxiStamp();
            taxiStamp.globalTime = LocalDateTime.of(2000, 1, 1, 18, 0, 0);
            taxiStamp.gps = new Coord(0, 0);
            taxiStamps.add(taxiStamp);
        }
        {
            TaxiStamp taxiStamp = new TaxiStamp();
            taxiStamp.globalTime = LocalDateTime.of(2000, 1, 1, 21, 0, 0);
            taxiStamp.gps = new Coord(3, 4);
            taxiStamp.occupied = true;
            taxiStamps.add(taxiStamp);
        }
        {
            TaxiStamp taxiStamp = new TaxiStamp();
            taxiStamp.globalTime = LocalDateTime.of(2000, 1, 1, 23, 0, 0);
            taxiStamp.gps = new Coord(2, 2);
            taxiStamp.occupied = true;
            taxiStamps.add(taxiStamp);
        }
    }

    @Test
    public void testBoundaries() {
        Optional<Tensor> optional = TaxiStampHelpers.boundaries(taxiStamps);
        Assert.assertTrue(optional.isPresent());
        Assert.assertEquals(Tensors.vector(0, 4, 0, 3), optional.get());
    }

    @Test
    public void testMinLng() {
        OptionalDouble optional = TaxiStampHelpers.minLng(taxiStamps);
        Assert.assertTrue(optional.isPresent());
        Assert.assertEquals(0, optional.getAsDouble(), 0);
    }

    @Test
    public void testMaxLng() {
        OptionalDouble optional = TaxiStampHelpers.maxLng(taxiStamps);
        Assert.assertTrue(optional.isPresent());
        Assert.assertEquals(3, optional.getAsDouble(), 0);
    }

    @Test
    public void testMinLat() {
        OptionalDouble optional = TaxiStampHelpers.minLat(taxiStamps);
        Assert.assertTrue(optional.isPresent());
        Assert.assertEquals(0, optional.getAsDouble(), 0);
    }

    @Test
    public void testMaxLat() {
        OptionalDouble optional = TaxiStampHelpers.maxLat(taxiStamps);
        Assert.assertTrue(optional.isPresent());
        Assert.assertEquals(4, optional.getAsDouble(), 0);
    }

    @Test
    public void testMinTime() {
        Optional<LocalDateTime> optional = TaxiStampHelpers.minTime(taxiStamps);
        Assert.assertTrue(optional.isPresent());
        Assert.assertEquals(LocalDateTime.of(2000, 1, 1, 9, 0, 0), optional.get());
    }

    @Test
    public void testMaxTime() {
        Optional<LocalDateTime> optional = TaxiStampHelpers.maxTime(taxiStamps);
        Assert.assertTrue(optional.isPresent());
        Assert.assertEquals(LocalDateTime.of(2000, 1, 1, 23, 0, 0), optional.get());
    }

    @Test
    public void testMinPickupTime() {
        Optional<LocalDateTime> optional = TaxiStampHelpers.minPickupTime(taxiStamps);
        Assert.assertTrue(optional.isPresent());
        Assert.assertEquals(LocalDateTime.of(2000, 1, 1, 10, 0, 0), optional.get());
    }

    @Test
    public void testMaxPickupTime() {
        Optional<LocalDateTime> optional = TaxiStampHelpers.maxPickupTime(taxiStamps);
        Assert.assertTrue(optional.isPresent());
        Assert.assertEquals(LocalDateTime.of(2000, 1, 1, 21, 0, 0), optional.get());
    }

    @Test
    public void testNumberOfRequests() {
        Assert.assertEquals(2, TaxiStampHelpers.numberOfRequests(taxiStamps));
    }

    @Test
    public void testPickupTimes() {
        Collection<LocalDateTime> pickupTimes = TaxiStampHelpers.pickupTimes(taxiStamps);
        Assert.assertEquals(TaxiStampHelpers.numberOfRequests(taxiStamps), pickupTimes.size());
        Assert.assertTrue(pickupTimes.containsAll(Arrays.asList( //
                LocalDateTime.of(2000, 1, 1, 10, 0, 0), //
                LocalDateTime.of(2000, 1, 1, 21, 0, 0)
        )));
    }
}
