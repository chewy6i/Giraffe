package core

import job.TestJob
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.slf4j.LoggerFactory

object Driver {
  val logger = LoggerFactory.getLogger("com")

  var sc: SparkContext = null

  var sparkMaster = "localhost"

  def run(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.WARN)

    logger.info("Starting Driver...")

    val optsMap = CmdLine.getOpts(args)
    optsMap.foreach(x => logger.info(x._1 + " = " + x._2))

    val appName = optsMap("appName")
    val sparkMaster = optsMap("sparkMaster")

    logger.info("Creating sparkContext")
    sc = SparkContextFactory.createSparkContext(sparkMaster, appName)
    logger.info("Creating hiveContext")
    val hiveContext = new org.apache.spark.sql.hive.HiveContext(sc)
    logger.info("Creating sqlContext")
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)

    logger.info("Run Job")

    TestJob.sc = sc
    TestJob.sqlContext = sqlContext
    TestJob.hiveContext = hiveContext


    TestJob.run()

    sc.stop()
    logger.info("Exiting Driver.")

  }

  def main(args: Array[String]): Unit = {
    run(args)
  }
}
