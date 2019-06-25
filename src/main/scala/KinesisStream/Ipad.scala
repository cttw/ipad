package KinesisStream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.alpakka.kinesis.scaladsl.KinesisSink
import akka.stream.scaladsl.{Flow, Source}
import akka.stream.{ActorMaterializer, Materializer, OverflowStrategy}
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClientBuilder
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import redis.RedisClient

import scala.io.StdIn

object Ipad extends App {
  
  
  implicit val system: ActorSystem = ActorSystem("iPAD")
  implicit val materializer = ActorMaterializer()
  
  implicit val amazonKinesisAsync: com.amazonaws.services.kinesis.AmazonKinesisAsync =
    AmazonKinesisAsyncClientBuilder.defaultClient()
  
  system.registerOnTermination(amazonKinesisAsync.shutdown())
  
  val redis = RedisClient()
  
  val flowSettings = Configurations.flowSettings
  
  val consumerConfig = Configurations.consumerConfig
  
  val objectMapper = new ObjectMapper()
  objectMapper.registerModule(DefaultScalaModule)
  
  case class Customer (customerName: String, customerId: String)
  case class Order (orderName: String, customer: Customer, status: String = "", source: String = "IPAD")
//  case class Status (order: Order, status: String)
  
  // listening to order status
  Subscribe.orderstatus.run()
  
  val cName: String = StdIn.readLine()
  val cId: String = cName + "-" + new scala.util.Random().nextInt(100)
  
  val source = Source.actorRef[Order](10, OverflowStrategy.dropHead)
  
  val flow: Flow[Order, PutRecordsRequestEntry, NotUsed] = Flow[Order].map[PutRecordsRequestEntry](order => {
    println(s"Order Status for customer ${order.customer.customerName} for order ${order.orderName}  : POSTED")
    Actions.createRecord(order)
  })
  
  
  // actor based publish graph
  val input = source.via(flow).to(KinesisSink("rd_test_stream", flowSettings)).run()
  
  val noOfOrders = StdIn.readInt()
  
  val customer = Customer(cName, cId)
  redis.hmset("customers",Map(customer.customerId -> "1"))
  for (i <- 1 to noOfOrders){
    val item = StdIn.readLine()
    
    val order = Order(item, customer)
    
    input ! order
    
  }
  
}
