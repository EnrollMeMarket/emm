package knbit.emm.algo.builder;

import knbit.emm.algo.data.MarketData;
import knbit.emm.model.Market;
import knbit.emm.model.Swap;
import knbit.emm.model.UClass;
import org.apache.log4j.Logger;
import org.jgrapht.graph.DirectedMultigraph;

public class GraphBuilder {

    private static final Logger logger = Logger.getLogger(GraphBuilder.class.getName());

    public static <V, E> DirectedMultigraph<V, E> build(Market market, MarketData marketData, VertexBuilder<V> vertexBuilder, EdgeBuilder<E> edgeBuilder, Class<E> edgeClass) {
        DirectedMultigraph<V, E> graph = new DirectedMultigraph<>(edgeClass);

        marketData.getuClasses().forEach(uClass -> {
            V vertex = vertexBuilder.build(uClass);
            graph.addVertex(vertex);
        });
        marketData.getSwaps().forEach(swap -> {
            E edge = edgeBuilder.build(swap);
            graph.addEdge(vertexBuilder.build(swap.getGive()), vertexBuilder.build(swap.getTake()), edge);
        });
        logger.info(String.format("Graph successfully built for market %s %d vertices %d edges",
                market.getName(), graph.vertexSet().size(), graph.edgeSet().size()));

        return graph;
    }


    @FunctionalInterface
    public interface VertexBuilder<V> {
        V build(UClass uClass);
    }

    @FunctionalInterface
    public interface EdgeBuilder<E> {
        E build(Swap swap);
    }
}
