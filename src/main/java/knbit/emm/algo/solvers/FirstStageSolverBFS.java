package knbit.emm.algo.solvers;


import knbit.emm.algo.graph.edge.Edge;
import knbit.emm.algo.graph.vertex.Vertex;
import knbit.emm.algo.scc.Scc;
import knbit.emm.algo.utils.pair.Pair;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedSubgraph;

import java.util.*;
import java.util.stream.Collectors;

public class FirstStageSolverBFS {


    /**
     * Performed algorithm has two stages:
     * 1. Calculate SCC
     * 2. Delete edges that are not included in calculated
     * <p>
     * Example usage:
     * DirectedGraph tmp;// some input knbit.emm.algo.graph
     * .
     * .
     * .
     * List<Edge> removedEdges = execute(tmp);//Performs algorithm and returns the list of declined edges (exchanges)
     *
     * @param inputGraph input knbit.emm.algo.graph
     */
    public static List<Edge> execute(DirectedGraph<Vertex, Edge> inputGraph) {
        List<Edge> declinedEdges = new ArrayList<>();
        commitSccChanges(inputGraph, declinedEdges);
        innerSecondStage(inputGraph, declinedEdges);
        return declinedEdges;
    }

    private static void innerSecondStage(DirectedGraph<Vertex, Edge> inputGraph, List<Edge> declinedEdges) {
        Map<Vertex, Integer> decliningInfluenceMap = new HashMap<>();
        List<Vertex> candidates = getCandidates(inputGraph, decliningInfluenceMap);
        Queue<Pair<Vertex, Edge>> bfsQueue = new ArrayDeque<>();
        Map<Edge, Edge> visitedFrom = new HashMap<>();
        executeStage(inputGraph, declinedEdges, decliningInfluenceMap, candidates, bfsQueue, visitedFrom);
    }

    private static void executeStage(DirectedGraph<Vertex, Edge> inputGraph, List<Edge> declinedEdges, Map<Vertex, Integer> decliningInfluenceMap, List<Vertex> candidates, Queue<Pair<Vertex, Edge>> bfsQueue, Map<Edge, Edge> visitedFrom) {
        while (!candidates.isEmpty()) {
            clearCollections(decliningInfluenceMap, bfsQueue, visitedFrom);
            bfsQueue.addAll(candidates.stream().filter(vertex -> vertexWeight(inputGraph, vertex, decliningInfluenceMap) < 0).map(v -> new Pair<Vertex, Edge>(v, null)).collect(Collectors.toList()));
            BFS(inputGraph, decliningInfluenceMap, bfsQueue, visitedFrom);
            candidates = getCandidates(inputGraph, decliningInfluenceMap);
            commitChanges(inputGraph, declinedEdges);
        }
    }

    private static void clearCollections(Map<Vertex, Integer> decliningInfluenceMap, Queue<Pair<Vertex, Edge>> bfsQueue, Map<Edge, Edge> visitedFrom) {
        visitedFrom.clear();
        bfsQueue.clear();
        decliningInfluenceMap.clear();
    }

    private static void BFS(DirectedGraph<Vertex, Edge> inputGraph, Map<Vertex, Integer> decliningInfluenceMap, Queue<Pair<Vertex, Edge>> bfsQueue, Map<Edge, Edge> visitedFrom) {
        Pair<Vertex, Edge> u;
        while (!bfsQueue.isEmpty()) {
            u = bfsQueue.poll();
            processVertex(inputGraph, decliningInfluenceMap, bfsQueue, visitedFrom, u);
        }
    }

    private static void processVertex(DirectedGraph<Vertex, Edge> inputGraph, Map<Vertex, Integer> decliningInfluenceMap, Queue<Pair<Vertex, Edge>> bfsQueue, Map<Edge, Edge> visitedFrom, Pair<Vertex, Edge> u) {
        inputGraph.outgoingEdgesOf(u.first).stream().filter(e -> !e.isDeclined() && !visitedFrom.containsKey(e)).forEach(e -> {
            visitedFrom.put(e, u.second);
            if (vertexWeight(inputGraph, inputGraph.getEdgeTarget(e), decliningInfluenceMap) <= 0) {
                bfsQueue.add(new Pair<>(inputGraph.getEdgeTarget(e), e));
            } else {
                goBack(inputGraph, decliningInfluenceMap, visitedFrom, e);
            }
        });
    }

    private static void goBack(DirectedGraph<Vertex, Edge> inputGraph, Map<Vertex, Integer> decliningInfluenceMap, Map<Edge, Edge> visitedFrom, Edge e) {
        Vertex edgeTarget = inputGraph.getEdgeTarget(e);
        Edge former = e;
        for (Edge tmp = e; tmp != null; tmp = visitedFrom.get(tmp)) {
            former = tmp;
        }
        processIfNegativeWeight(inputGraph, decliningInfluenceMap, visitedFrom, e, edgeTarget, former);
    }

    private static void processIfNegativeWeight(DirectedGraph<Vertex, Edge> inputGraph, Map<Vertex, Integer> decliningInfluenceMap, Map<Edge, Edge> visitedFrom, Edge e, Vertex edgeTarget, Edge former) {
        decliningInfluenceMap.put(edgeTarget, decliningInfluenceMap.getOrDefault(edgeTarget, 0) - 1);
        edgeTarget = inputGraph.getEdgeSource(former);
        decliningInfluenceMap.put(edgeTarget, decliningInfluenceMap.getOrDefault(edgeTarget, 0) + 1);
        for (Edge tmp = e; tmp != null; tmp = visitedFrom.get(tmp)) {
            tmp.decline();
        }
    }

    private static void commitSccChanges(DirectedGraph<Vertex, Edge> inputGraph, List<Edge> declinedEdges) {
        List<DirectedSubgraph<Vertex, Edge>> subGraphList = Scc.getListOfSCCSubGraphs(inputGraph);
        deleteUnnecessaryEdges(inputGraph, subGraphList);
        commitChanges(inputGraph, declinedEdges);
    }

    private static void deleteUnnecessaryEdges(DirectedGraph<Vertex, Edge> inputGraph, List<DirectedSubgraph<Vertex, Edge>> subGraphList) {
        inputGraph.edgeSet().forEach(edge -> {
            if (!subGraphList.stream().filter(subgraph -> subgraph.edgeSet().contains(edge)).findAny().isPresent()) {
                edge.decline();
            }
        });
    }

    private static List<Vertex> getCandidates(DirectedGraph<Vertex, Edge> exchangeGraph, Map<Vertex, Integer> decliningInfluenceMap) {
        return exchangeGraph.vertexSet().stream().filter(vertex -> vertexWeight(exchangeGraph, vertex, decliningInfluenceMap) < 0)
                .sorted((vertex1, vertex2) -> vertexWeight(exchangeGraph, vertex2, decliningInfluenceMap) - vertexWeight(exchangeGraph, vertex1, decliningInfluenceMap))
                .collect(Collectors.toList());
    }

    private static void commitChanges(DirectedGraph<Vertex, Edge> exchangeGraph, List<Edge> declinedEdges) {
        Set<Edge> tmpEdgeSet = exchangeGraph.edgeSet().stream().filter(Edge::isDeclined).collect(Collectors.toSet());
        declinedEdges.addAll(tmpEdgeSet);
        exchangeGraph.removeAllEdges(tmpEdgeSet);
    }

    private static int vertexWeight(DirectedGraph<Vertex, Edge> exchangeGraph, Vertex v, Map<Vertex, Integer> decliningInfluenceMap) {
        return exchangeGraph.inDegreeOf(v) - exchangeGraph.outDegreeOf(v) + decliningInfluenceMap.getOrDefault(v, 0);
    }
}
