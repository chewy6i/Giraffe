import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

  case class Skills(name: String, skills: String) extends Serializable

  val conf = new SparkConf().setMaster("local").setAppName("spark-play")
  val sc = new SparkContext(conf)

  val users: RDD[(VertexId, Skills)] =
    sc.parallelize(Array((3L, Skills("sridhar", "java")), (7L, Skills("pete", "java")),
      (5L, Skills("job1", "java")), (2L, Skills("shekhar", "java"))))
  val relationships: RDD[Edge[String]] =
    sc.parallelize(Array(Edge(3L, 7L, "colleague"), Edge(5L, 3L, "hiring"),
      Edge(2L, 7L, "colleague"), Edge(5L, 7L, "hiring")))
  val graph = Graph(users, relationships)

  graph.edges.filter { case Edge(src, dst, prop) => prop.equals("hiring") }.take(10)

