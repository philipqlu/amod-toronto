/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.tripmodif;

import java.io.File;

import org.apache.commons.io.FileUtils;

public enum NullModifier implements TaxiDataModifier {
    INSTANCE;

    @Override
    public File modify(File taxiData) throws Exception {
        /** fast previous version, do again */
        File outFile = new File(taxiData.getAbsolutePath().replace(".csv", "_modified.csv"));
        FileUtils.copyFile(taxiData, outFile);
        return outFile;
    }
}
