package knbit.emm.algo;

import knbit.emm.algo.builder.DependentEdgesBuilder;
import knbit.emm.algo.builder.GraphBuilder;
import knbit.emm.algo.data.MarketData;
import knbit.emm.algo.graph.edge.Edge;
import knbit.emm.algo.graph.vertex.Vertex;
import knbit.emm.algo.solvers.DependentEdgesStage;
import knbit.emm.algo.solvers.FirstStageSolver;
import knbit.emm.model.Market;
import knbit.emm.model.Swap;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Solver {

    private final Market market;
    private final MarketData marketData;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final DependentEdgesBuilder dependentEdgesBuilder;

    public Solver(Market market, MarketData marketData) {
        this.market = market;
        this.marketData = marketData;
        dependentEdgesBuilder = new DependentEdgesBuilder(marketData);
    }


    private Set<Swap> solve() {
        DirectedMultigraph<Vertex, Edge> graph = GraphBuilder.build(market, marketData, Vertex::new, Edge::new, Edge.class);
        List<Set<Edge>> dependentEdgesSets = dependentEdgesBuilder.getAllDependencySets();
        new DependentEdgesStage(graph, dependentEdgesSets).execute();
        FirstStageSolver.FullFirstStageAlgorithm(graph);
        return graph.edgeSet().stream().map(Edge::getSwap).collect(Collectors.toSet());
    }


    public Future<Set<Swap>> execute() {
        Callable<Set<Swap>> task = this::solve;
        return executorService.submit(task);
    }
}
