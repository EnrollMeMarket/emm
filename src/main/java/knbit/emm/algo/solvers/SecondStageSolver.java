package knbit.emm.algo.solvers;

import knbit.emm.algo.graph.edge.Edge;
import knbit.emm.algo.graph.vertex.Vertex;
import knbit.emm.algo.scc.Scc;
import knbit.emm.algo.utils.pair.Pair;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedSubgraph;

import java.util.*;
import java.util.stream.Collectors;

public class SecondStageSolver {

    /**
     * This static method remove conditional edges from the knbit.emm.algo.graph
     *
     * @return List of edges removed from the knbit.emm.algo.graph
     */
    public static List<Edge> execute(DirectedGraph<Vertex, Edge> input, List<Set<Edge>> dependentEdges, Map<Edge, Set<Edge>> dependsFrom) {
        List<Edge> declinedEdges = new ArrayList<>();
        getSccChanges(input, dependentEdges);
        commitToSets(dependentEdges, dependsFrom);
        commitChanges(input, declinedEdges);
        dependentEdges.removeIf(Set::isEmpty);
        for (Set<Edge> edgeSet : dependentEdges) {
            processEdgeSet(input, declinedEdges, edgeSet);
        }
        return declinedEdges;
    }

    private static void processEdgeSet(DirectedGraph<Vertex, Edge> input, List<Edge> declinedEdges, Set<Edge> edgeSet) {
        Vertex start = input.getEdgeSource(edgeSet.stream().findFirst().get()); // ugly
        Edge min = edgeSet.stream().min((edge, t1) -> vertexWeight(input, input.getEdgeTarget(edge)) - vertexWeight(input, input.getEdgeTarget(t1))).get();
        edgeSet.stream().filter(e -> vertexWeight(input, input.getEdgeTarget(e)) > vertexWeight(input, input.getEdgeTarget(min))).forEach(Edge::decline);
        edgeSet.removeIf(Edge::isDeclined);
        if (edgeSet.size() > 1) {
            Map<Edge, Integer> bfsOut = edgeSet.stream().collect(Collectors.toMap(edge -> edge, edge -> BFS(input, start, edge)));
            bfsOut.forEach((edge, integer) -> edge.decline());
            bfsOut.keySet().stream().min((edge, t1) -> bfsOut.get(edge) - bfsOut.get(t1)).get().accept();
        }
        edgeSet.removeIf(Edge::isDeclined);
        commitChanges(input, declinedEdges);
    }

    private static void commitToSets(List<Set<Edge>> dependentEdges, Map<Edge, Set<Edge>> dependsFrom) {
        for (Set<Edge> dependencySet : dependentEdges) {
            dependencySet.removeIf(Edge::isDeclined);
        }
        dependsFrom.keySet().removeIf(Edge::isDeclined);
    }

    /**
     * Declining edges for all included in the dependent set
     */
    private static void getSccChanges(DirectedGraph<Vertex, Edge> inputGraph, List<Set<Edge>> dependentEdges) {
        List<DirectedSubgraph<Vertex, Edge>> subGraphList = Scc.getListOfSCCSubGraphs(inputGraph);
        for (Edge e : inputGraph.edgeSet()) {
            if (dependentEdges.stream().reduce(false, (aBoolean, edgeSet) -> aBoolean = aBoolean || edgeSet.contains(e), (aBoolean1, aBoolean2) -> aBoolean1 || aBoolean2) &&
                    subGraphList.stream().reduce(true, (aBoolean3, vertexEdgeDirectedSubGraph) -> aBoolean3 = aBoolean3 && !vertexEdgeDirectedSubGraph.edgeSet()
                            .contains(e), (aBoolean4, aBoolean21) -> aBoolean4 && aBoolean21)) {
                e.decline();
            }
        }
    }

    /**
     * Weight function for priority of choice
     */
    private static int vertexWeight(DirectedGraph<Vertex, Edge> exchangeGraph, Vertex v) {
        return exchangeGraph.inDegreeOf(v) - exchangeGraph.outDegreeOf(v);
    }

    /**
     * This method returns length of the cycle start -> start
     *
     * @return length of the cycle start -> start through the edge edgeStart
     */
    private static Integer BFS(DirectedGraph<Vertex, Edge> inputGraph, Vertex start, Edge edgeStart) {
        Queue<Pair<Vertex, Integer>> bfsQueue = new ArrayDeque<>();
        bfsQueue.add(new Pair<>(inputGraph.getEdgeTarget(edgeStart), 1));
        Set<Vertex> visited = new HashSet<>();
        int returnValue = 0;
        while (!bfsQueue.isEmpty() && !visited.contains(start)) {
            returnValue = processVertexBFS(inputGraph, start, bfsQueue, visited, returnValue);
        }
        return returnValue;
    }

    private static int processVertexBFS(DirectedGraph<Vertex, Edge> inputGraph, Vertex start, Queue<Pair<Vertex, Integer>> bfsQueue, Set<Vertex> visited, int returnValue) {
        Pair<Vertex, Integer> pollPair = bfsQueue.poll();
        visited.add(pollPair.first);
        for (Vertex v : inputGraph.outgoingEdgesOf(pollPair.first).stream()
                .filter(edge1 -> !visited.contains(inputGraph.getEdgeTarget(edge1)))
                .map(inputGraph::getEdgeTarget).collect(Collectors.toList())) {
            if (v != start) {
                bfsQueue.add(new Pair<>(v, pollPair.second + 1));
            } else {
                returnValue = pollPair.second + 1;
                visited.add(start);
            }
        }
        return returnValue;
    }

    private static void commitChanges(DirectedGraph<Vertex, Edge> exchangeGraph, List<Edge> declinedEdges) {
        Set<Edge> tmpEdgeSet = exchangeGraph.edgeSet().stream().filter(Edge::isDeclined).collect(Collectors.toSet());
        declinedEdges.addAll(tmpEdgeSet);
        exchangeGraph.removeAllEdges(tmpEdgeSet);
    }

}
