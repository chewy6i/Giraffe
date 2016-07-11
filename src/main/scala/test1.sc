
import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

trait Node{ var id: String}

case class Resume(var id: String, skills: String) extends Node

case class Job(var id: String, skills: String) extends Node
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

import org.apache.spark.graphx.lib.ShortestPaths
val v1 = 101L
val v2 = 1L

val result = ShortestPaths.run(graph, Seq(v2))
val shortestPath = result               // result is a graph
  .vertices                             // we get the vertices RDD
  .filter({case(vId, _) => vId == v1})  // we filter to get only the shortest path from v1
  .first                                // there's only one value
  ._2                                   // the result is a tuple (v1, Map)
  .get(v2)                              // we get its shortest path to v2 as an Option object


