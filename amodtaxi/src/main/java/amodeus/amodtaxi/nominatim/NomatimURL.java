/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.nominatim;

import java.util.List;

public enum NomatimURL {
    ;

    /** @return a url to request a geo JSON from the nominatim API for the
     *         query including the {@link List} of Strings @param elements, usage
     *         example:
     * 
     *         String httpToQuery =
     *         NominatimURL.build(Arrays.asList("135","pilkington","avenue","birmingham")); */
    public static String build(List<String> elements) {
        String queryInsert = "";

        for (int i = 0; i < elements.size(); ++i) {
            if (i < elements.size() - 1)
                queryInsert = queryInsert + elements.get(i) + "+";
            else
                queryInsert = queryInsert + elements.get(i);
        }

        // API to query geoJSON
        String https_url = "https://nominatim.openstreetmap.org/search?q="//
                + queryInsert//
                + "&format=geojson";
        return https_url;

    }

}
