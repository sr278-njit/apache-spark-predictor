#!/bin/bash
export JAVA_HOME=/usr/bin/java
export SPARK_LOG_DIR=~/logs

export SPARK_MASTER_PORT=7077
export SPARK_MASTER_WEBUI_PORT=8090
export SPARK_MASTER_LOG=$SPARK_LOG_DIR/spark-master.out

export SPARK_WORKER_LOG=$SPARK_LOG_DIR/spark-worker.out
export SPARK_WORKER_WEBUI_PORT=8080
export SPARK_WORKER_PORT=7000

SPARK_WORKLOAD=$1
SPARK_MASTER_IP=$2

mkdir -p $SPARK_LOG_DIR

if [ "$SPARK_WORKLOAD" == "master" ];
then
    export SPARK_MASTER_HOST=`hostname`
    cd /opt/spark/bin
    sudo nohup ./spark-class org.apache.spark.deploy.master.Master --ip $SPARK_MASTER_HOST --port $SPARK_MASTER_PORT --webui-port $SPARK_MASTER_WEBUI_PORT >> $SPARK_MASTER_LOG 2>&1 &
    echo "$SPARK_WORKLOAD started"
elif [ "$SPARK_WORKLOAD" == "worker" ];
then
    if [ -z "$SPARK_MASTER_IP" ]
    then
        echo "\$SPARK_MASTER_IP is empty"
    else
        echo "\$SPARK_MASTER_IP is NOT empty"
        cd /opt/spark/bin
        sudo nohup ./spark-class org.apache.spark.deploy.worker.Worker --webui-port $SPARK_WORKER_WEBUI_PORT $SPARK_MASTER_IP >> $SPARK_WORKER_LOG 2>&1 &
        echo "$SPARK_WORKLOAD started"
    fi
elif [ "$SPARK_WORKLOAD" == "stop" ];
then
    sudo kill $(ps aux | grep 'spark' | grep -v grep | awk '{print $2}') >/dev/null 2>&1
    echo "Spark processes stopped"
fi
