/* amodeus - Copyright (c) 2019, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.tripmodif;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import amodeus.amodeus.taxitrip.ExportTaxiTrips;
import amodeus.amodeus.taxitrip.ImportTaxiTrips;
import amodeus.amodeus.taxitrip.TaxiTrip;
import amodeus.amodtaxi.util.NamingConvention;

public class TaxiDataModifierCollection implements TaxiDataModifier {

    private final List<TripModifier> modifiers = new ArrayList<>();

    public void addModifier(TripModifier modifier) {
        modifiers.add(modifier);
    }

    @Override // from TaxiDataModifier
    public final File modify(File taxiData) throws Exception {
        /** gather all original trips */
        List<TaxiTrip> originals = new ArrayList<>(ImportTaxiTrips.fromFile(taxiData));

        /** notify about all the taxi trips */
        originals.forEach(taxiTrip -> //
                modifiers.forEach(modifier -> modifier.notify(taxiTrip)));

        /** let modifiers do modifications on each trip, then return */
        List<TaxiTrip> modified = new ArrayList<>();
        originals.forEach(taxiTrip -> {
            TaxiTrip changed = taxiTrip;
            for (TripModifier tripModifier : modifiers)
                changed = tripModifier.modify(changed);
            modified.add(changed);
        });
        File outFile = new File(taxiData.getParentFile(), NamingConvention.similarTo(taxiData).apply("modified"));
        ExportTaxiTrips.toFile(modified.stream(), outFile);
        return outFile;
    }
}
