package job

import org.apache.spark.graphx.{Edge, EdgeTriplet, Graph, VertexId}
import org.apache.spark.rdd.RDD
import org.slf4j.LoggerFactory

/*job posting
 # jobId	metadata
 0	{"company":"XYZ", "title":"Software engineer", "salary":"90000-120000",...}

  */

/*Resume
 # ResumeId	metadata
 100	{"experience":"5-10years","skills":["java","scala"....],...}
  */


/*Job_Resume
# jobId	ResumeId
0	100
0 101
5 100
 */

sealed trait NodeType
case object JOB extends NodeType
case object RESUME extends NodeType


trait VNode{ var nodeType: NodeType}

case class JobNode(var nodeType: NodeType, metadata: String) extends VNode

case class ResumeNode(var nodeType: NodeType, metadata: String) extends VNode

case class JRLink(fScore: Double, rScore: Double)

object BipartiteMatchingJob extends JobTrait {
  val logger = LoggerFactory.getLogger("com")


  val job_info = "/Users/salla/headhunt/datasets/comp_info.tsv"
  val job_resume = "/Users/salla/headhunt/datasets/ingr_comp.tsv"
  val resume_info = "/Users/salla/headhunt/datasets/ingr_info.tsv"


  override def run(): Unit = {
    logger.info("BipartiteMatchingJob run")

    val jobs: RDD[(VertexId, VNode)] =
      sc.textFile(job_info).
        filter(! _.startsWith("#")).
        map {line =>
          val row = line split '\t'
          (row(0).toLong, JobNode(JOB, row(2)))
        }

    val resumes: RDD[(VertexId, VNode)] =
      sc.textFile(resume_info).
        filter(! _.startsWith("#")).
        map {line =>
          val row = line split '\t'
          (10000L + row(0).toLong, ResumeNode(RESUME, row(2)))
        }

    val links: RDD[Edge[JRLink]] =
      sc.textFile(job_resume).
        filter(! _.startsWith("#")).
        map {line =>
          val row = line split '\t'
          Edge(row(0).toLong, 10000L + row(1).toLong, JRLink(0.7, 0.6))
        }


    println("jobs " + jobs.count)
    println("resumes " + resumes.count)
    println("links " + links.count)

    val nodes = jobs ++ resumes

    println("nodes " + nodes.count)
    val jobNetwork = Graph(nodes, links)

    println(jobNetwork.vertices.take(10))
    println(jobNetwork.edges.take(10))

    def showTriplet(t: EdgeTriplet[VNode,JRLink]): String = {
      "Job " ++ t.srcId.toString ++ " - fScore: %f , rScore: %f - ".format(t.attr.fScore, t.attr.rScore) ++ " Resume " ++ t.dstId.toString
    }

    jobNetwork.triplets.take(10).
      foreach(showTriplet _ andThen println _)

    println("Done")

  }

}

