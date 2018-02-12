package knbit.emm.algo.solvers;

import knbit.emm.algo.graph.edge.Edge;
import knbit.emm.algo.graph.vertex.Vertex;
import knbit.emm.algo.scc.Scc;
import knbit.emm.algo.utils.pair.Pair;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedSubgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;


public class FirstStageSolver {

    /**
     * Method committing Changes done to subGraph to its baseGraph,
     * it removes any edges which were chosen to be declined
     *
     * @param graph - knbit.emm.algo.graph for which the changes are ought to be committed
     * @return amount of affected edges
     */
    public static int commitChanges(DirectedGraph<Vertex, Edge> graph, List<DirectedSubgraph<Vertex, Edge>> editedSubGraphsList) {
        List<Edge> edgeList = new ArrayList<>(graph.edgeSet());
        System.out.println(graph.edgeSet().size());
        int counter = graph.edgeSet().size();
        edgeList.stream().filter(edge -> !editedSubGraphsList.stream().filter(e -> e.containsEdge(edge))
                .findAny().isPresent()).forEach(edge -> {
            graph.removeEdge(edge);
            edge.decline();
        });
        return counter - graph.edgeSet().size();
    }

    /**
     * This method performs "First Stage" algorithm until it has some impact on the knbit.emm.algo.graph
     *
     * @param graph input knbit.emm.algo.graph
     */
    public static void FullFirstStageAlgorithm(DirectedGraph<Vertex, Edge> graph) {
        List<DirectedSubgraph<Vertex, Edge>> tmpSubGraphList;
        do {
            tmpSubGraphList = Scc.getListOfSCCSubGraphs(graph);
            FirstStageSolver.execute(tmpSubGraphList);
        } while (FirstStageSolver.commitChanges(graph, tmpSubGraphList) > 0);
    }

    /**
     * This method return weight of the given edge
     * Weight function assumed by algorithm (e=(v,u)):
     * w(e)=min(-deg(v),deg(u))
     *
     * @param subGraph the owner of processing edge
     * @param e        the edge to process
     * @return weight function value
     */
    public static int calculateWeight(DirectedGraph<Vertex, Edge> subGraph, Edge e) {
        return Integer.min(-(subGraph.inDegreeOf(subGraph.getEdgeSource(e)) - subGraph.outDegreeOf(subGraph.getEdgeSource(e))),
                subGraph.inDegreeOf(subGraph.getEdgeTarget(e)) - subGraph.outDegreeOf(subGraph.getEdgeTarget(e)));
    }

    public static void execute(List<DirectedSubgraph<Vertex, Edge>> editedSubGraphsList) {
        for (DirectedSubgraph<Vertex, Edge> subGraph : editedSubGraphsList) {
            PriorityQueue<Pair<Integer, Edge>> queue = new PriorityQueue<>((e1, e2) -> e2.first - e1.first);
            queue.addAll(subGraph.edgeSet().stream().map(e -> new Pair<>(calculateWeight(subGraph, e), e)).collect(Collectors.toList()));
            while (!queue.isEmpty() && calculateWeight(subGraph, queue.peek().second) > 0) {
                Pair<Integer, Edge> tmpHead = queue.poll();
                if (!subGraph.edgeSet().contains(tmpHead.second)) {
                    continue;
                }
                Set<Edge> incomingEdges = subGraph.incomingEdgesOf(subGraph.getEdgeSource(tmpHead.second));
                Set<Edge> outgoingEdges = subGraph.outgoingEdgesOf(subGraph.getEdgeTarget(tmpHead.second));
                Set<Edge> parallelEdges = subGraph.getAllEdges(subGraph.getEdgeSource(tmpHead.second), subGraph.getEdgeTarget(tmpHead.second));
                subGraph.removeEdge(tmpHead.second);
                queue.addAll(incomingEdges.stream().map(e -> new Pair<>(calculateWeight(subGraph, e), e)).collect(Collectors.toList()));
                queue.addAll(outgoingEdges.stream().map(e -> new Pair<>(calculateWeight(subGraph, e), e)).collect(Collectors.toList()));
                queue.addAll(parallelEdges.stream().map(e -> new Pair<>(calculateWeight(subGraph, e), e)).collect(Collectors.toList()));
            }
        }
    }

    private static List<Pair<Integer, Integer>> printGraph(DirectedGraph<Vertex, Edge> graph) {
        return graph.edgeSet().stream().map(Edge::getSwap).map(swap -> new Pair<>(swap.getGive().getClassId(), swap.getTake().getClassId())).collect(Collectors.toList());
    }

    private static Pair<Integer, Integer> printEdge(Edge edge) {
        return new Pair<>(edge.getSwap().getGive().getClassId(), edge.getSwap().getTake().getClassId());
    }

    private static void printQueue(PriorityQueue<Pair<Integer, Edge>> queue) {
        System.out.println(queue.stream().sorted((p1, p2) -> p1.first - p2.first).map(p -> p.second)
                .map(Edge::getSwap)
                .map(swap -> new Pair<>(swap.getGive().getClassId(), swap.getTake().getClassId()))
                .collect(Collectors.toList()));
    }
}
