package net.njit.apache.spark.trainer.configuration;

import org.apache.spark.sql.SparkSession;

public class SparkConfiguration {


    public static SparkSession getSparkSession(String appName) {
        SparkSession s = SparkSession
                .builder()
                .appName(appName)
                .master("local[*]")
                .config("spark.master", "local")
                .config("spark.eventLog.enabled", "false")
                .config("spark.shuffle.service.enabled", "false")
                .config("spark.dynamicAllocation.enabled", "false")
                .config("spark.io.compression.codec", "snappy")
                .config("spark.rdd.compress", "true")
                .getOrCreate();
        s.sparkContext().setLogLevel("ERROR");
        return s;
    }

}
