package net.njit.apache.spark.trainer.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSanitizer {

    private static final String SANITIZED = "Sanitized";

    public static String getSanitizedFilePath(String property) throws IOException {
        String trainingDataset = System.getenv(property);

        System.out.println(property + " value is: " + trainingDataset);

        if(trainingDataset == null || trainingDataset.isEmpty()) {
            throw new RuntimeException("trainingDataSet environment must be set");
        }

        Path path = Paths.get(trainingDataset);
        Charset charset = StandardCharsets.UTF_8;

        String content = new String(Files.readAllBytes(path), charset);
        content = content.replaceAll("\"", "")
                .replaceAll(" ", "_");

        String sanitizedFilename = String.format("%s_%s", SANITIZED,
                path.getFileName().toString());
        Path sPath = Paths.get(path.getParent().toString() + "/" + sanitizedFilename);
        Files.write(sPath, content.getBytes(charset));
        return sPath.toAbsolutePath().toString();


    }
}
