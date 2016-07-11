package test

import job.JobTrait
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.slf4j.LoggerFactory


object TestJob extends JobTrait {
  val logger = LoggerFactory.getLogger("com")

  trait Node{ var id: String}

  case class Resume(var id: String, skills: String) extends Node

  case class Job(var id: String, skills: String) extends Node

  override def run(): Unit = {
    logger.info("TestJob run")

    val nodes: RDD[(VertexId, Node)] =
      sc.parallelize(Array(
        (1L, Resume("sridhar", "java")),
        (2L, Resume("pete", "java")),
        (3L, Resume("shekhar", "java")),
        (4L, Resume("john", "html")),

        (101L, Job("job1", "java")),
        (102L, Job("job2", "html"))
      ))

    val links: RDD[Edge[String]] =
      sc.parallelize(Array(
        Edge(101L, 1L, "hire"),
        Edge(101L, 2L, "hire"),
        Edge(101L, 3L, "hire"),
        Edge(102L, 4L, "hire")
      ))

    val graph = Graph(nodes, links)

    val res = graph.edges.filter { case Edge(src, dst, prop) => prop.equals("hiring") }.take(10)

    res.foreach(r => logger.info(r.toString))

    val facts: RDD[String] = graph.triplets.map(triplet => triplet.srcAttr.id +
      " is the " + triplet.attr + " of " + triplet.dstAttr.id)
    facts.collect.foreach(println(_))

    val ns = graph.collectNeighbors(EdgeDirection.Either)
    ns.take(10)

    graph.connectedComponents()

    graph.vertices.take(10)
    graph.edges.take(10)
    graph.connectedComponents.vertices.take(10)
    graph.connectedComponents.edges.take(10)


  }
}
