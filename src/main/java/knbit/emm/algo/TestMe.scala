package knbit.emm.algo

import org.jgrapht.alg.StrongConnectivityInspector
import org.jgrapht.graph.SimpleDirectedGraph

import scala.util.Random

object TestMe {
  val graph = new SimpleDirectedGraph[Int, Int](classOf[Int])
  graph.addVertex(1)
  graph.addVertex(2)
  graph.addVertex(3)
  graph.addVertex(4)
  graph.addVertex(5)
  val ps = for (i <- 1 to 5; j <- 1 to 5 if i != j && Random.nextBoolean()) yield i -> j
  for ( (i, j) <- ps) graph.addEdge(i, j, 5 * i + j)

  def apply(x: Int): Unit = {
    println(graph)
    val conectivity = new StrongConnectivityInspector(graph)
    println(conectivity.stronglyConnectedSubgraphs())
  }

}
