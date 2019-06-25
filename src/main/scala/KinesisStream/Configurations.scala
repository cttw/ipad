package KinesisStream

import akka.stream.alpakka.kinesis.KinesisFlowSettings
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.{InitialPositionInStream, KinesisClientLibConfiguration}

import scala.concurrent.duration._

object Configurations {
  
  val flowSettings = KinesisFlowSettings
    .create()
    .withParallelism(1)
    .withMaxBatchSize(500)
    .withMaxRecordsPerSecond(1000)
    .withMaxBytesPerSecond(1000000)
    .withMaxRetries(5)
    .withBackoffStrategy(KinesisFlowSettings.Exponential)
    .withRetryInitialTimeout(100.milli)
  
  
  val consumerConfig = new KinesisClientLibConfiguration(
      "ipad",
      "rd_test_stream",
      new DefaultAWSCredentialsProviderChain,
      s"kinesisWorker-${java.util.UUID.randomUUID().toString}"
    )
    .withRegionName("us-east-2")
    .withCallProcessRecordsEvenForEmptyRecordList(true)
    .withInitialPositionInStream(InitialPositionInStream.TRIM_HORIZON)
  
}
