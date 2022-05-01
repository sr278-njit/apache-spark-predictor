package net.njit.apache.spark.trainer.configuration;

import org.apache.spark.sql.SparkSession;

public class SparkConfiguration {


    public static SparkSession getSparkSession(String appName) {
        String masterIp = System.getenv("MASTER_IP");
        if (masterIp == null || masterIp.isEmpty()) {
            throw new RuntimeException("MASTER_IP must be set");
        }
        SparkSession s = SparkSession
                .builder()
                .appName(appName)
                .master(masterIp)
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
