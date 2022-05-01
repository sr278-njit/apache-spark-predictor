package net.njit.apache.spark.trainer;

import net.njit.apache.spark.trainer.model.SparkModel;
import net.njit.apache.spark.trainer.service.SparkService;
import org.apache.spark.ml.classification.RandomForestClassificationModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.IOException;

public class PredictionApplication {

    public static void main(String[] args) throws IOException {
        String modelPath = System.getenv("MODEL_PATH");

        if(modelPath == null || modelPath.isEmpty()) {
            throw new RuntimeException("modelPath env var must be set and point to model");
        }

        System.out.println("Loading RandomForestClassificationModel from: " + modelPath);
        RandomForestClassificationModel model = RandomForestClassificationModel.load(modelPath);
        SparkModel sparkModel = new SparkService().getDatasetForModel("predictor", "VALIDATION_DATASET");
        Dataset<Row> predictionRows = model.transform(sparkModel.getRows()).cache();

        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("label")
                .setPredictionCol("prediction");

        evaluator.setMetricName("accuracy");
        System.out.println("Prediction Model Accuracy is: " + evaluator.evaluate(predictionRows));

        evaluator.setMetricName("f1");
        double f1 = evaluator.evaluate(predictionRows);
        System.out.println("Prediction Model F1 score is: " + f1);

        sparkModel.getSparkSession().stop();
        System.exit(0);
    }
}
