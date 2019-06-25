package KinesisStream

//import KinesisStream.Ipad.Status
import KinesisStream.Ipad.Order
import akka.stream.scaladsl.Sink
import com.contxt.kinesis.KinesisSource
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object Subscribe {
  
  val config = Configurations.consumerConfig
  
  val objectMapper = new ObjectMapper()
  objectMapper.registerModule(DefaultScalaModule)
  
  val orderstatus = KinesisSource(config).map(record => {record.markProcessed()
  record.data.utf8String}).map(order => objectMapper.readValue(order, classOf[Order])).filter(order => order.source == "RESTAURANT")
    .to(Sink.foreach(order => println(s"Restaurant's reply for customer ${order.customer.customerName} with order STATUS ${order.orderName} : ${order.status}")))
}
