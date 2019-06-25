package KinesisStream

import java.nio.ByteBuffer

import KinesisStream.Ipad.Order
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object Actions {
  
  val objectMapper = new ObjectMapper()
  objectMapper.registerModule(DefaultScalaModule)
  
  def createRecord(order: Order): PutRecordsRequestEntry = {
    new PutRecordsRequestEntry().withData(ByteBuffer.wrap(objectMapper.writeValueAsBytes(order)).asReadOnlyBuffer()).withPartitionKey("part-1")
  }
  
}


