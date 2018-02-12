package knbit.emm.algo.scc;

import knbit.emm.algo.graph.edge.Edge;
import knbit.emm.algo.graph.vertex.Vertex;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DirectedSubgraph;

import java.util.List;
import java.util.Set;

public class Scc {

    /**
     * This method process given graph and using StrongConnectivityInspector from jgrapht lib,
     * returns list of strong-connected subgraphs
     *
     * @param graph directed graph for processing
     * @return list of strong-connected subgraphs
     */
    public static List<DirectedSubgraph<Vertex, Edge>> getListOfSCCSubGraphs(DirectedGraph graph) {
        @SuppressWarnings("unchecked") StrongConnectivityInspector<Vertex, Edge> strongConnectivityInspector = new StrongConnectivityInspector<>(graph);
        return strongConnectivityInspector.stronglyConnectedSubgraphs();
    }

    /**
     * This method process given graph and using StrongConnectivityInspector from jgrapht lib,
     * and return true if it is strongly connected, otherwise false
     *
     * @param graph directed graph for processing
     * @return true if graph is strongly connected, otherwise false
     */
    public static boolean isStronglyComponent(DirectedGraph graph) {
        @SuppressWarnings("unchecked") StrongConnectivityInspector<Vertex, Edge> strongConnectivityInspector = new StrongConnectivityInspector<>(graph);
        return strongConnectivityInspector.isStronglyConnected();
    }

    /**
     * This method process given graph and using StrongConnectivityInspector from jgrapht lib,
     * and return list of sets, which contain vertices of one strongly connected component
     *
     * @param graph directed graph for processing
     * @return list of sets, which contain vertices of one strongly connected component
     */
    public static List<Set<Vertex>> getStronglyConnectedSets(DirectedGraph graph) {
        @SuppressWarnings("unchecked") StrongConnectivityInspector<Vertex, Edge> strongConnectivityInspector = new StrongConnectivityInspector<>(graph);
        return strongConnectivityInspector.stronglyConnectedSets();
    }
}
