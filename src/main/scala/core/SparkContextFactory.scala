package core

import org.apache.spark.{SparkConf, SparkContext, SparkEnv}
import org.slf4j.LoggerFactory

object SparkContextFactory {
  val logger = LoggerFactory.getLogger("com")

  def createSparkContext(
    sparkMaster: String,
    appName: String
  ): SparkContext = {

    val conf = new SparkConf()
      .setAppName(appName)

    sparkMaster match {
      case "local" => conf.setMaster("local")
      case "yarncluster" => conf.setMaster("yarn-cluster")
      case "yarnclient" => conf.setMaster("yarn-client")
      case _ => conf.setMaster(sparkMaster)
    }

    conf.set("spark.eventLog.enabled", "false")
    conf.set("spark.eventLog.dir", "/tmp")

    conf.set("spark.scheduler.mode", "FAIR")
    conf.set("spark.memory.useLegacyMode", "false")
    conf.set("spark.memory.storageFraction", "0.75")
    conf.set("spark.memory.fraction", "0.75")
    conf.set("spark.yarn.executor.memoryOverhead", "600")
    conf.set("spark.network.timeout", "1200")
    conf.set("spark.akka.timeout", "1200")
    conf.set("spark.rpc.askTimeout", "1200")
    conf.set("spark.rpc.lookupTimeout", "1200")
    conf.set("spark.executor.heartbeatInterval", "30")
    conf.set("spark.rpc.numRetries", "6")
    conf.set("spark.task.maxFailures", "8")

    val allconf = conf.getAll
    allconf.foreach(c => {
      logger.info(c._1 + " = " + c._2)
    })

    val sc = new SparkContext(conf)
    val ac = SparkEnv.get.conf.getAkkaConf

    ac.foreach(acc => println(acc.toString()))

    sc

  }

}
