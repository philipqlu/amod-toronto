/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package amodeus.amodtaxi.tripmodif;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ChicagoFormatModifier implements TaxiDataModifier {
    private final String removeMe = "\"";
    private final String taxiCompanyNameOld = "Taxicab Insurance Agency, LLC";
    private final String taxiCompanyNameNew = "Taxicab Insurance Agency LLC";

    @Override
    public File modify(File taxiData) throws Exception {
        File outFile = new File(taxiData.getAbsolutePath().replace(".csv", "_prepared.csv"));
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(taxiData)); //
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outFile))) {
            System.out.println("INFO start data correction");
            bufferedReader.lines().forEachOrdered(line -> {
                try {
                    String lineNew = line.replace(removeMe, "");
                    lineNew = lineNew.replace(taxiCompanyNameOld, taxiCompanyNameNew);
                    bufferedWriter.write(lineNew);
                    bufferedWriter.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("INFO successfully stored corrected data in " + outFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outFile;
    }

}
