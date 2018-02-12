package knbit.emm.algo.solvers

import java.util.{List => JList, Set => JSet}

import knbit.emm.algo.graph.edge.Edge
import knbit.emm.algo.graph.vertex.Vertex
import knbit.emm.model.{Student, Swap, UClass}
import org.jgrapht.DirectedGraph
import org.jgrapht.alg.StrongConnectivityInspector
import org.jgrapht.graph.{DirectedMultigraph, DirectedSubgraph, SimpleDirectedGraph}
import org.jgrapht.traverse.BreadthFirstIterator

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.{Map => MMap}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

class BasicAlgorithm(inputGraph: DirectedGraph[Vertex, Edge], dependentEdges: JList[JSet[Edge]]) {

  private[solvers] val sccGraph: DirectedGraph[Vertex, Edge] = {
    val subGraphs = new StrongConnectivityInspector(inputGraph).stronglyConnectedSubgraphs()
    val vertices = subGraphs.iterator().asScala.flatMap(_.vertexSet().asScala).toSet
    val edges = subGraphs.iterator().asScala.flatMap(_.edgeSet().asScala).toSet
    val graph = new DirectedMultigraph[Vertex, Edge](classOf[Edge])
    for (v <- vertices) graph.addVertex(v)
    for (e <- edges) graph.addEdge(inputGraph.getEdgeSource(e), inputGraph.getEdgeTarget(e), e)
    graph
  }

  def handleDependentEdges: Set[Edge] = {

    def bfsEdges(e: Edge): Int = {
      val iterator = new BreadthFirstIterator(sccGraph, sccGraph.getEdgeSource(e))
      val distances = Map(sccGraph.getEdgeSource(e) -> 0)
      (iterator.asScala foldLeft distances) { (distances, vertex) =>
        distances ++ sccGraph.outgoingEdgesOf(vertex).iterator().asScala.map(sccGraph.getEdgeTarget).map(v => (v, distances(vertex) + 1))
      }.values.max
    }

    def processDependentEdges(edges: mutable.Set[Edge]): Stream[Edge] =
      edges.toStream.filter(sccGraph.containsEdge)
        .sortBy(bfsEdges).reverse.tail

    val res = dependentEdges
      .iterator().asScala
      .map(_.asScala)
      .flatMap(processDependentEdges)
      .toSet
    res
  }

  def execute(): Set[Edge] = {
    handleDependentEdges.foreach(sccGraph.removeEdge)
    new StrongConnectivityInspector(sccGraph)
      .stronglyConnectedSubgraphs()
      .iterator().asScala
      .flatMap(_.edgeSet().asScala)
      .toSet
  }

  def smartExecute(): DirectedGraph[Vertex, Edge] = {
    import knbit.emm.algo.utils.GraphUtils._
    handleDependentEdges.foreach(sccGraph.removeEdge)
    new StrongConnectivityInspector(sccGraph).stronglyConnectedSubgraphs().asScala.map(_.asInstanceOf[DirectedGraph[Vertex, Edge]]).reduceLeft(_ add _)
  }

}

object BasicAlgorithm {

  def conductTest(): Unit = {

    val classes = (for (i <- 0 to 6) yield {
      val uclass = new UClass()
      uclass.setClassId(i)
      i -> uclass
    }).toMap
    val vertices = classes map { case (ind, clazz) => ind -> new Vertex(clazz) }
    val pairs = List(0 -> 1, 0 -> 2, 0 -> 3, 1 -> 2, 2 -> 3, 3 -> 1, 3 -> 0, 3 -> 4, 4 -> 5, 4 -> 6, 5 -> 6, 6 -> 5)
    val edges = pairs map {
      case (aa, bb) =>
        val swp = new Swap(new Student, classes(bb), classes(aa))
        swp.setSwapId(6 * aa + bb)
        (aa, bb) -> new Edge(swp)
    }

    val testGraph = {
      val graph = new SimpleDirectedGraph[Vertex, Edge](classOf[Edge])
      for ((_, vertex) <- vertices) graph.addVertex(vertex)
      for (((aa, bb), edge) <- edges) graph.addEdge(vertices(aa), vertices(bb), edge)
      graph
    }

    Try {
      assert(testGraph.vertexSet().size() == 7, "graph build error")
      assert(testGraph.edgeSet().size == 12, "graph build error")
    } flatMap { _ =>
      val inspector = new StrongConnectivityInspector(testGraph)
      val subgraphs: JList[DirectedSubgraph[Vertex, Edge]] = inspector.stronglyConnectedSubgraphs()
      Try {
        assert(subgraphs.size() == 3, s"wrong number of subgraphs: ${subgraphs.size()} when expected is 3")
        assert(subgraphs.asScala.map(_.vertexSet().asScala).toSet == List(List(0, 1, 2, 3), List(4), List(5, 6)).map(_.map(vertices).to[mutable.Set]).toSet, "wrong contents of subgraphs")
        "Passed"
      }
    } match {
      case Success(s) => println(s)
      case Failure(e) => e.printStackTrace()
    }
  }

  def conductSecondTest(testDependency: Boolean = false): Unit = {

    val classes = (for (i <- 0 to 4) yield i -> {
      val uclass = new UClass
      uclass.setClassId(i)
      uclass
    }).toMap

    val vertices = classes.map { case (ind, clazz) => ind -> new Vertex(clazz)}

    val cycleA = List(0 -> 1, 1 -> 2, 2 -> 0)
    val cycleB = List(2 -> 3, 3 -> 4, 4 -> 2)

    def studentSource(a: Int, b: Int): Student = {
      val student = new Student()
      student.setStudentId(s"$a:$b")
      student
    }

    val edges = cycleA ++ cycleB map {
      case (aa, bb) =>
        val swp = new Swap(studentSource(aa, bb), classes(bb), classes(aa))
        swp.setSwapId(6 * aa + bb)
        (aa, bb) -> new Edge(swp)
    }

    val dependentSet = edges.collect{ case (p@(aa, bb), edge) if p == (1, 2) || p == (2, 3) => edge}.toSet.asJava
    val testGraph = {
      val graph = new DirectedMultigraph[Vertex, Edge](classOf[Edge])
      for ((_, vertex) <- vertices) graph.addVertex(vertex)
      for (((aa, bb), edge) <- edges) graph.addEdge(vertices(aa), vertices(bb), edge)
      graph
    }
    val alg = new BasicAlgorithm(testGraph, if (testDependency) List(dependentSet).asJava else List.empty[JSet[Edge]].asJava)
    Try {
      if (testDependency) {
        require(alg.execute() == edges.collect {
          case ((aa, bb), edge) if aa == 0 && bb == 1 => edge
          case ((aa, bb), edge) if aa == 1 && bb == 2 => edge
          case ((aa, bb), edge) if aa == 2 && bb == 0 => edge
        }.toSet)
      } else {
        require(alg.execute() == edges.iterator.map(_._2).toSet)
      }
    } match {
      case Success(_) => println("Passed")
      case Failure(e) => e.printStackTrace()
    }

  }

}


