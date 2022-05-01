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
        s.sparkContext()
                .hadoopConfiguration().set("fs.s3a.access.key", System.getenv("AWS_ACCESS_KEY_ID"));
        // Replace Key with your AWS secret key (You can find this on IAM
        s.sparkContext()
                .hadoopConfiguration().set("fs.s3a.secret.key", System.getenv("AWS_SECRET_ACCESS_KEY"));
        s.sparkContext()
                .hadoopConfiguration().set("fs.s3a.endpoint", "s3.amazonaws.com");
        s.sparkContext().setLogLevel("ERROR");
        return s;
    }

}
