package knbit.emm.algo

import java.util.{Set => JSet}

import knbit.emm.algo.builder.{DependentEdgesBuilder, GraphBuilder}
import knbit.emm.algo.data.MarketData
import knbit.emm.algo.graph.edge.Edge
import knbit.emm.algo.graph.vertex.Vertex
import knbit.emm.algo.solvers.{BasicAlgorithm, IterationAlgorithm}
import knbit.emm.model.{Market, Swap}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

class ScalaSolver(market: Market, marketData: MarketData)(implicit executionContext: ExecutionContext) {
  private val dependentEdgesBuilder = new DependentEdgesBuilder(marketData)

  def solve(): Future[JSet[Swap]] = Future {
    val graph = GraphBuilder.build[Vertex, Edge](market, marketData, uclass => new Vertex(uclass), swap => new Edge(swap), classOf[Edge])
    val dependentEdges = dependentEdgesBuilder.getAllDependencySets
    val newGraph = new BasicAlgorithm(graph, dependentEdges).smartExecute()
    IterationAlgorithm.finalGraph(newGraph).edgeSet().iterator().asScala.map(_.getSwap).to[Set].asJava
  }


}
