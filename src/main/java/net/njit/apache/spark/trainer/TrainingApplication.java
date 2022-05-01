package net.njit.apache.spark.trainer;

import net.njit.apache.spark.trainer.model.SparkModel;
import net.njit.apache.spark.trainer.service.SparkService;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.RandomForestClassificationModel;
import org.apache.spark.ml.classification.RandomForestClassificationSummary;
import org.apache.spark.ml.classification.RandomForestClassifier;

import java.io.IOException;

public class TrainingApplication {

    public static void main(String[] args) throws IOException {

        SparkModel sparkModel = new SparkService().getDatasetForModel("trainer", "TRAINING_DATASET");

        System.out.println("Training RandomForestClassificationModel with dataset: " + sparkModel.getDatasetPath());
        RandomForestClassifier randomForestClassifier = new RandomForestClassifier()
                .setLabelCol("label")
                .setFeaturesCol("features")
                .setMaxDepth(5);

        Pipeline pipeline = new Pipeline();
        pipeline.setStages(new PipelineStage[]{randomForestClassifier});
        PipelineModel pipelineModel = pipeline.fit(sparkModel.getRows());

        RandomForestClassificationModel model = (RandomForestClassificationModel) (pipelineModel.stages())[0];
        System.out.println("Gathering RandomForestClassificationModel summary for dataset: " + sparkModel.getDatasetPath());
        RandomForestClassificationSummary rfSummary = model.summary();
        System.out.println("Prediction Model Accuracy is: " + rfSummary.accuracy());
        System.out.println("Prediction Model F1 score is: " + rfSummary.weightedFMeasure());
        model.write().overwrite().save(System.getenv("OUTPUT_PATH"));

        sparkModel.getSparkSession().close();
        System.exit(0);
    }

}