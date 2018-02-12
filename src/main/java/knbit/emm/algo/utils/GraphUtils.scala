package knbit.emm.algo.utils

import org.jgrapht.Graph
import org.jgrapht.graph.{DirectedMultigraph, SimpleGraph}

import scala.collection.JavaConverters._
import scala.language.implicitConversions
import scala.reflect.ClassTag

object GraphUtils {

  implicit class CreationUtils[V, E](private val graph: Graph[V, E]) extends AnyVal {

    def copy[G <: Graph[V, E]](implicit instanceProvider: () => G) : G = {
      val vertices = graph.vertexSet()
      val edges = graph.edgeSet()
      val graphCopy = instanceProvider()
      for (v <- vertices.asScala) graphCopy.addVertex(v)
      for (e <- edges.asScala) graphCopy.addEdge(graph.getEdgeSource(e), graph.getEdgeTarget(e), e)
      graphCopy
    }

    def add[G <: Graph[V, E]](secondGraph: G)(implicit instanceProvider: () => G) : G = {
      val gCopy = copy[G]
      val vertices = secondGraph.vertexSet().asScala
      val edges = secondGraph.edgeSet().asScala
      for (v <- vertices) gCopy.addVertex(v)
      for (e <- edges) gCopy.addEdge(secondGraph.getEdgeSource(e), secondGraph.getEdgeTarget(e), e)
      gCopy
    }

    def graphEq(secondGraph: Graph[V, E]): Boolean =
      graph.vertexSet().equals(secondGraph.vertexSet()) &&
      graph.edgeSet().equals(secondGraph.edgeSet())

  }

  implicit def dirMultiGraphProvider[V, E](implicit tag: ClassTag[E]): () => DirectedMultigraph[V, E] =
    () => new DirectedMultigraph(tag.getClass.asInstanceOf[Class[E]])

  implicit def simpleGraphProvider[V, E](implicit tag: ClassTag[E]): () => SimpleGraph[V, E] =
    () => new SimpleGraph(tag.getClass.asInstanceOf[Class[E]])

}
