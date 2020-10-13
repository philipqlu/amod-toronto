/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.scenario.sanfrancisco;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import amodeus.amodeus.util.math.GlobalAssert;

/* package */ enum DateSelectSF {
    ;

    /** @return Collection of all {@link LocalDate} for wich data is recorded in the San Francisco taxi dataset,
     *         should be: 2008,05,18 - 2008,05,19 - ... - 2008,06,09
     * 
     *         2008-05-17 is not a complete day and therefore not considered
     *         2008-06-10 is almost empty, few requests found.
     * 
     *         Totally there are 23 relevant dates. */
    public static Collection<LocalDate> allRelevant() {
        Collection<LocalDate> dates = new ArrayList<>();
        LocalDate initial = LocalDate.of(2008, 05, 18);
        dates.add(initial);
        for (int i = 1; i < 23; ++i) {
            LocalDate ld = initial.plusDays(i);
            dates.add(ld);
        }
        return dates;
    }

    /** @return selected date */
    public static Collection<LocalDate> specific(int month, int day) {
        int year = 2008;
        GlobalAssert.that(month == 5 || month == 6);
        if (month == 5)
            GlobalAssert.that(day >= 18 && day <= 31);
        if (month == 6)
            GlobalAssert.that(day >= 1 && day <= 9);
        Collection<LocalDate> dates = new ArrayList<>();
        LocalDate only = LocalDate.of(year, month, day);
        dates.add(only);
        return dates;
    }
}
