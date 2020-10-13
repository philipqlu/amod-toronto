/* amodtaxi - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.util;

import java.time.LocalDate;
import java.time.Month;

import amodeus.amodtaxi.util.LocalDateConvert;
import junit.framework.TestCase;

public class LocalDateConvertTest extends TestCase {
    public void testSimple() {
        LocalDate localDate = LocalDateConvert.ofOptions("2014/11/18");
        assertEquals(localDate.getYear(), 2014);
        assertEquals(localDate.getMonth(), Month.NOVEMBER);
        assertEquals(localDate.getDayOfMonth(), 18);
    }
}
