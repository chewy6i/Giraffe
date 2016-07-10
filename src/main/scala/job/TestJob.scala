package job

import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.slf4j.LoggerFactory


object TestJob extends JobTrait {
  val logger = LoggerFactory.getLogger("com")

  case class Skills(name: String, skills: String) extends Serializable

  override def run(): Unit = {
    logger.info("TestJob run")


    val users: RDD[(VertexId, Skills)] =
      sc.parallelize(Array((3L, Skills("sridhar", "java")), (7L, Skills("pete", "java")),
        (5L, Skills("job1", "java")), (2L, Skills("shekhar", "java"))))
    val relationships: RDD[Edge[String]] =
      sc.parallelize(Array(Edge(3L, 7L, "colleague"),    Edge(5L, 3L, "hiring"),
        Edge(2L, 7L, "colleague"), Edge(5L, 7L, "hiring")))
    val graph = Graph(users, relationships)

    val res = graph.edges.filter { case Edge(src, dst, prop) => prop.equals("hiring") }.take(10)

    res.foreach(r => logger.info(r.toString))

  }
}
