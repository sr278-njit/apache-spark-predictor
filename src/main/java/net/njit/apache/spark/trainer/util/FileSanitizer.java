package net.njit.apache.spark.trainer.util;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
        writeSanitizedToS3(sanitizedFilename, sPath.toAbsolutePath().toString());
        return sPath.toAbsolutePath().toString();

    }

    private static void writeSanitizedToS3(String fileName, String path) throws IOException {
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new EnvironmentVariableCredentialsProvider())
                .withRegion(Regions.US_WEST_2)
                .build();

        String bucketName = System.getenv("BUCKET_NAME");

        if(bucketName == null || bucketName.isEmpty()) {
            throw new RuntimeException("BUCKET_NAME environment must be set");
        }

        s3client.putObject(bucketName, fileName, new File(path));

        InputStream in = s3client.getObject(bucketName, fileName).getObjectContent();
        Files.copy(in, Paths.get(path));
    }
}
