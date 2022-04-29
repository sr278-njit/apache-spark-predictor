package net.njit.apache.spark.trainer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

@Getter
@AllArgsConstructor
public class SparkModel {

    private final SparkSession sparkSession;

    private final Dataset<Row> rows;

    private final String datasetPath;

}
