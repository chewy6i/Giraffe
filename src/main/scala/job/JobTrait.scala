package job

import org.apache.spark.SparkContext
import org.apache.spark.sql._

/**
  * Created by salla on 7/9/16.
  */
trait JobTrait {

  var sc: SparkContext = null
  var hiveContext: hive.HiveContext = null
  var sqlContext: SQLContext = null


  def run()
}
