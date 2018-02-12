package knbit.emm.algo.solvers;

import knbit.emm.algo.graph.edge.Edge;
import knbit.emm.algo.graph.vertex.Vertex;
import knbit.emm.algo.scc.Scc;
import knbit.emm.algo.utils.pair.Pair;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedSubgraph;

import java.util.*;
import java.util.stream.Collectors;

public class DependentEdgesStage {

    private final DirectedGraph<Vertex, Edge> inputGraph;
    private final List<Set<Edge>> dependentEdges;

    public DependentEdgesStage(DirectedGraph<Vertex, Edge> inputGraph, List<Set<Edge>> dependentEdges) {
        this.inputGraph = inputGraph;
        this.dependentEdges = dependentEdges;
    }

    /**
     * This method returns the length of the shortest cycle starting and ending with the edge e
     * This length is equal the minimal number of egdes to remove in case of removing edge e
     *
     * @param e edge from which the cycle is starting
     * @return length of the cycle
     */
    private Integer bfsEdges(Edge e) {
        Queue<Pair<Vertex, Integer>> bfsQueue = new ArrayDeque<>();
        Set<Vertex> visited = new HashSet<>();
        Vertex source = inputGraph.getEdgeSource(e);
        bfsQueue.add(new Pair<>(inputGraph.getEdgeTarget(e), 1)); // starting from the end of the edge
        Integer dist = 0;
        while (!bfsQueue.isEmpty() && !visited.contains(source)) {
            Pair<Vertex, Integer> current = bfsQueue.poll();
            visited.add(current.first);
            List<Pair<Vertex, Integer>> tmp = inputGraph
                    .outgoingEdgesOf(current.first)
                    .stream()
                    .filter(edge -> !visited.contains(inputGraph.getEdgeTarget(edge)))
                    .map(edge -> new Pair<>(inputGraph.getEdgeTarget(edge), current.second + 1))
                    .collect(Collectors.toList());
            dist = Integer.max(dist, current.second + 1);
            bfsQueue.addAll(tmp);
        }
        return dist;
    }

    /**
     * This function returns value
     */
    private Set<Edge> dropBadEdges(Set<Edge> dependentSet) {
        Set<Edge> tmp = dependentSet.stream()
                .filter(edge -> !edge.isDeclined())
                .map(edge -> new Pair<>(edge, bfsEdges(edge)))
                .sorted((o1, o2) -> o2.second - o1.second)
                .skip(1).map(edgeIntegerPair -> edgeIntegerPair.first).collect(Collectors.toSet());
        tmp.forEach(Edge::decline);
        return tmp;
    }

    /**
     * List of all edges to remove
     */
    private List<Edge> dropAllBadEdges() {
        return dependentEdges
                .stream()
                .map(this::dropBadEdges)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * @return list of remaining edges!
     */
    public List<Edge> execute() {
        //remove edges not belonging to any scc, simple optimisation
        Set<Edge> sccEdges = Scc.getListOfSCCSubGraphs(inputGraph)
                .stream()
                .map(DirectedSubgraph::edgeSet)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        List<Edge> rejectedEdges = inputGraph.edgeSet().stream().filter(edge -> !sccEdges.contains(edge)).collect(Collectors.toList());
        rejectedEdges.forEach(edge -> {
            edge.decline();
            inputGraph.removeEdge(edge);
        });
        dependentEdges.forEach(set -> set.removeIf(Edge::isDeclined));
        //performing algorithm itself
        List<Edge> toDrop = dropAllBadEdges();
        toDrop.forEach(inputGraph::removeEdge);
        return Scc.getListOfSCCSubGraphs(inputGraph)
                .stream()
                .map(DirectedSubgraph::edgeSet)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}
