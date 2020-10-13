/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CSVUtils {
    ;

    /** @param line
     * @param regex e.g., separator = ";"
     * @return */
    public static List<String> csvLineToList(String line, String regex) {
        return Stream.of(line.split(regex)).collect(Collectors.toList());
    }

}
