echo "validation file is $VALIDATION_DATASET"
echo "Model path is $MODEL_PATH"
[ -d "$MODEL_PATH" ] && echo "Directory $MODEL_PATH exists." || echo "Error: Directory $MODEL_PATH does not exists."
ls "$VALIDATION_DATASET"
mvn exec:java -Dexec.mainClass="net.njit.apache.spark.trainer.PredictionApplication" -DvalidationDataset=$VALIDATION_DATASET -DmodelPath=$MODEL_PATH -Dspark.master=local[*] -Dspark.app.name=predictor -e >> $SPARK_WORKER_LOG
