package amodeus.amodtaxi.scenario.data;

import java.util.List;

import amodeus.amodeus.util.AmodeusTimeConvert;
import amodeus.amodtaxi.trace.TaxiStamp;

public interface TaxiStampReader {
    /** create a {@link TaxiStamp} from
     * @param dataRow {@link List<String>}
     * @return {@link TaxiStamp} */
    TaxiStamp read(List<String> dataRow);

    /** @return {@link AmodeusTimeConvert} used */
    AmodeusTimeConvert timeConvert();
}
