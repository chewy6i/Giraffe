package test

import job.JobTrait
import org.apache.spark.graphx.{Edge, EdgeTriplet, Graph, VertexId}
import org.apache.spark.rdd.RDD
import org.slf4j.LoggerFactory

/*ingr_info
 # id	ingredient name	category
 0	magnolia_tripetala	flower
 1	calyptranthes_parriculata	plant
 2	chamaecyparis_pisifera_oil	plant derivative
 3	mackerel	fish/seafood
 4	mimusops_elengi_flower	flower
 5	hyssop	herb

  */

/*comp_info
 # id	Compound name	CAS number
 0	jasmone	488-10-8
 1	5-methylhexanoic_acid	628-46-6
 2	l-glutamine	56-85-9
 3	1-methyl-3-methoxy-4-isopropylbenzene	1076-56-8
 4	methyl-3-phenylpropionate	103-25-3
 5	3-mercapto-2-methylpentan-1-ol_(racemic)	227456-27-1
  */


/*ingr_comp
# ingredient id	compound id
1392	906
1259	861
1079	673
22	906
103	906
1005	906

 */

object TestBipartiteMatching extends JobTrait {
  val logger = LoggerFactory.getLogger("com")

  trait FNNode{ var name: String}

  case class Ingredient(var name: String, category: String) extends FNNode

  case class Compound(var name: String, cas: String) extends FNNode

  val comp_info = "/Users/salla/headhunt/datasets/comp_info.tsv"
  val ingr_comp = "/Users/salla/headhunt/datasets/ingr_comp.tsv"
  val ingr_info = "/Users/salla/headhunt/datasets/ingr_info.tsv"


  override def run(): Unit = {
    logger.info("TestBipartiteMatching run")

    val ingredients: RDD[(VertexId, FNNode)] =
      sc.textFile(ingr_info).
        filter(! _.startsWith("#")).
        map {line =>
          val row = line split '\t'
          (row(0).toLong, Ingredient(row(1), row(2)))
        }

    val compounds: RDD[(VertexId, FNNode)] =
      sc.textFile(comp_info).
        filter(! _.startsWith("#")).
        map {line =>
          val row = line split '\t'
          (10000L + row(0).toLong, Compound(row(1), row(2)))
        }

    val links: RDD[Edge[Int]] =
      sc.textFile(ingr_comp).
        filter(! _.startsWith("#")).
        map {line =>
          val row = line split '\t'
          Edge(row(0).toLong, 10000L + row(1).toLong, 1)
        }


    println("ingredients " + ingredients.count)
    println("compounds " + compounds.count)
    println("links " + links.count)

    val nodes = ingredients ++ compounds

    println("nodes " + nodes.count)
    val foodNetwork = Graph(nodes, links)

    println(foodNetwork.vertices.take(10))
    println(foodNetwork.edges.take(10))

    def showTriplet(t: EdgeTriplet[FNNode,Int]): String = {
      "The ingredient " ++ t.srcAttr.name ++ " contains " ++ t.dstAttr.name
    }

    val facts: RDD[String] = foodNetwork.triplets.map(t => t.srcAttr.name +
      " is the " + t.attr + " of " + t.dstAttr.name)
    facts.collect.foreach(println(_))

    foodNetwork.triplets.take(5).
      foreach(showTriplet _ andThen println _)

    println("Done")

  }

}

