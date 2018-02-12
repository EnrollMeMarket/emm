package knbit.emm.algo.solvers

import java.util.{List => JList}

import knbit.emm.algo.graph.edge.Edge
import knbit.emm.algo.graph.vertex.Vertex
import knbit.emm.algo.scc.Scc
import knbit.emm.algo.utils.GraphUtils._
import org.jgrapht.DirectedGraph
import org.jgrapht.alg.DijkstraShortestPath
import org.jgrapht.graph.DirectedMultigraph
import org.jgrapht.traverse.BreadthFirstIterator

import scala.collection.JavaConverters._

object IterationAlgorithm {

  private def nextStep(subgraphs: List[DirectedGraph[Vertex, Edge]]): DirectedGraph[Vertex, Edge] =
    subgraphs.reduceLeftOption(_ add _).map(removeDA).getOrElse(new DirectedMultigraph(classOf[Edge]))

  def finalGraph(graph: DirectedGraph[Vertex, Edge]): DirectedGraph[Vertex, Edge] = {
    val sccList = Scc.getListOfSCCSubGraphs(graph)
    FirstStageSolver.execute(sccList)
    val next = nextStep(sccList.asScala.toList)
    if (graph graphEq next) {
      next
    } else {
      finalGraph(next)
    }
  }

  private def removeDA(graph: DirectedGraph[Vertex, Edge]): DirectedGraph[Vertex, Edge] = findAnyDonor(graph).flatMap(findPathToClosestAcceptor(graph, _)) match {
    case Some(edges) =>
      val copy = graph.copy[DirectedMultigraph[Vertex, Edge]]
      copy.removeAllEdges(edges)
      copy
    case None =>
      graph
  }

  private def findAnyDonor(graph: DirectedGraph[Vertex, Edge]): Option[Vertex] = graph.vertexSet().asScala
    .find(v => graph.outDegreeOf(v) - graph.inDegreeOf(v) > 0)

  private def findAnyAcceptor(graph: DirectedGraph[Vertex, Edge]): Option[Vertex] = graph.vertexSet().asScala
    .find(v => graph.inDegreeOf(v) - graph.outDegreeOf(v) > 0)

  private def findPathToClosestAcceptor(graph: DirectedGraph[Vertex, Edge], vertex: Vertex): Option[JList[Edge]] = {
    val bfsIterator = new BreadthFirstIterator(graph, vertex).asScala
    bfsIterator.find(v => graph.inDegreeOf(v) - graph.outDegreeOf(v) > 0).map { end =>
      DijkstraShortestPath.findPathBetween(graph, vertex, end)
    }
  }
}

