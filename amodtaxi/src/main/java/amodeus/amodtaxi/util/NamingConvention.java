package amodeus.amodtaxi.util;

import java.io.File;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.FilenameUtils;

public class NamingConvention {
    public static NamingConvention similarTo(File file) {
        return new NamingConvention(FilenameUtils.getBaseName(file.getPath()), FileNameUtils.getExtension(file.getPath()));
    }

    public static NamingConvention using(String baseName, String extension) {
        return new NamingConvention(baseName, extension);
    }

    // ---

    private final String baseName;
    private final String extension;

    private NamingConvention(String baseNme, String extension) {
        this.baseName = baseNme;
        this.extension = extension.startsWith(".") //
                ? extension.substring(1) //
                : extension;
    }

    public String apply(String... middles) {
        return String.format("%s_%s.%s", baseName, String.join("_", middles), extension);
    }
}
