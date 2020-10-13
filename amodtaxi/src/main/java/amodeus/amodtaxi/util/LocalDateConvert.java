/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.util;

import java.time.LocalDate;

public enum LocalDateConvert {
    ;

    public static LocalDate ofOptions(String dateString) {
        String[] split = dateString.split("/");
        return LocalDate.of( //
                Integer.parseInt(split[0]), //
                Integer.parseInt(split[1]), //
                Integer.parseInt(split[2]));
    }
}
