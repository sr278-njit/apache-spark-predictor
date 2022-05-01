package net.njit.apache.spark.trainer.service;

import net.njit.apache.spark.trainer.configuration.SparkConfiguration;
import net.njit.apache.spark.trainer.model.SparkModel;
import net.njit.apache.spark.trainer.util.FileSanitizer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import scala.collection.JavaConverters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SparkService {

    private static final List<String> featureColumns = Arrays.asList("alcohol",
            "sulphates",
            "pH",
            "density",
            "free sulfur dioxide",
            "total sulfur dioxide",
            "chlorides",
            "residual sugar",
            "citric acid",
            "volatile acidity",
            "fixed acidity");

    private static final String UNDER_SCORE = "_";

    public SparkModel getDatasetForModel(String appName, String datasetPathProperty) throws IOException {
        SparkSession sparkSession
                = SparkConfiguration.getSparkSession(appName);
        String datasetPath = FileSanitizer.getSanitizedFilePath(datasetPathProperty);
        Dataset<Row> r = getDataset(sparkSession, datasetPath);
        System.out.println("Loaded dataset: " + datasetPath);
        castToDouble(r);

        r = r.withColumnRenamed("quality", "label");

        List<String> santizedFeatureColumns = new ArrayList<>(featureColumns).stream()
                .map(value -> value.replace(" ", UNDER_SCORE))
                .collect(Collectors.toList());

        santizedFeatureColumns.add("label");
        Dataset<Row> rows = r.selectExpr(JavaConverters
                .asScalaIteratorConverter(santizedFeatureColumns.iterator()).asScala().toSeq());

        santizedFeatureColumns.remove("label");
        VectorAssembler assembler =
                new VectorAssembler().setInputCols(santizedFeatureColumns.stream()
                        .toArray(String[]::new)).setOutputCol("features");

        return new SparkModel(sparkSession, assembler.transform(rows).select("label", "features"), datasetPath);
    }

    private Dataset<Row> getDataset(SparkSession s, String datasetPath) throws IOException {
        Dataset<Row> hi = s.read().option("header", true)
                .option("multiline", true)
                .option("delimiter", ";")
                .option("quote", "\"")
                .option("inferSchema", true)
                .csv("s3a://cc-assignmnt-2/Sanitized_TrainingDataset.csv");

        hi.printSchema();

        return s.read().format("csv")
                .option("header", true)
                .option("multiline", true)
                .option("delimiter", ";")
                .option("quote", "\"")
                .option("inferSchema", true)
                .load(datasetPath);
    }

    private static void castToDouble(Dataset<Row> r) {
        for (String c : r.columns()) {
            r = r.withColumn(c, r.col(c).cast("double"));
        }
    }
}
