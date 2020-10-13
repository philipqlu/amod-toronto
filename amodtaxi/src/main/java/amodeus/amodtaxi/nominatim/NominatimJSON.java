/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.nominatim;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum NominatimJSON {
    ;

    /** @return coordinates as {@link Tensor} in format
     *         {long,lat} for the json response of the Nominatim URL passed
     *         as @param queryJSON */
    public static Tensor toCoordinates(JSONObject queryJSON) {

        try {
            Object obj1 = queryJSON.get("features");
            JSONArray ja1 = (JSONArray) obj1;
            JSONObject jo1 = ja1.getJSONObject(0);
            JSONObject jo2 = jo1.getJSONObject("geometry");
            JSONArray ja2 = jo2.getJSONArray("coordinates");
            String lng = ja2.get(0).toString();
            String lat = ja2.get(1).toString();
            Tensor loc = Tensors.vector(Double.parseDouble(lng), Double.parseDouble(lat));

            return loc;
        } catch (JSONException exception) {
            System.err.println("could not extract coordinates from Nominatim JSON...");
            // exception.printStackTrace();
        }
        return null;
    }

}
