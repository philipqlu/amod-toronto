package amodeus.amodtaxi.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public enum ResourceHandling {
    ;

    public static void copy(String source, Path target, boolean replaceExisting) throws Exception {
        copy(ResourceHandling.class, source, target, replaceExisting);
    }

    public static void copy(Class clazz, String source, Path target, boolean replaceExisting) throws Exception {
        CopyOption[] options = replaceExisting ? //
                new CopyOption[] { StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING } : //
                new CopyOption[] { StandardCopyOption.COPY_ATTRIBUTES };
        File tmp = getResourceAsFile(clazz, source.startsWith("/") ? source : ("/" + source));
        Files.copy(Path.of(tmp.getAbsolutePath()), target, options);
    }

    private static File getResourceAsFile(Class clazz, String resourcePath) {
        try (InputStream in = clazz.getResourceAsStream(resourcePath)) {
            if (Objects.isNull(in))
                return null;

            File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                // copy stream
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1)
                    out.write(buffer, 0, bytesRead);
            }
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
