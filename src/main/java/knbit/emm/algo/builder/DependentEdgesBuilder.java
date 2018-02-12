package knbit.emm.algo.builder;

import knbit.emm.algo.data.MarketData;
import knbit.emm.algo.data.TermBox;
import knbit.emm.algo.graph.edge.Edge;
import knbit.emm.model.Student;
import knbit.emm.model.Swap;
import knbit.emm.model.UClass;

import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DependentEdgesBuilder {

    private final MarketData marketData;

    public class ScatteredEdge {
        private final Swap baseSwap;
        private final TermBox edgeTerm;

        public ScatteredEdge(Swap baseSwap, TermBox edgeTerm) {
            this.baseSwap = baseSwap;
            this.edgeTerm = edgeTerm;
        }

        public Swap getBaseSwap() {
            return baseSwap;
        }

        public TermBox getEdgeTerm() {
            return edgeTerm;
        }
    }

    public DependentEdgesBuilder(MarketData marketData) {
        this.marketData = marketData;
    }

    public List<Set<Edge>> getAllDependencySets() {
        return Stream.concat(getAllSourceDependentSwaps(), getAllTargetDependingSwapsAdvanced())
                .map(swaps -> swaps.stream().map(Edge::new).collect(Collectors.toSet())).collect(Collectors.toList());
    }

    private Stream<Set<Swap>> getAllSourceDependentSwaps() {
        Stream<List<Swap>> swapsStream =  marketData.getStudents().stream().map(Student::getSwaps);
        Stream<Map<UClass, List<Swap>>> sourceGroupedSwaps = swapsStream.map(swaps -> swaps.stream().collect(Collectors.groupingBy(Swap::getGive)));
        return sourceGroupedSwaps.flatMap(this::getDependentEdgesFrom);
    }

    private Stream<Set<Swap>> getDependentEdgesFrom(Map<UClass, List<Swap>> source) {
        return source.values().stream().map(LinkedHashSet::new);
    }

    public List<TermBox> possibleTermBoxes(Collection<UClass> terms) {
        Stream<Time> beginingTimes = terms.stream().map(UClass::getBegTime);
        Stream<Time> endTimes = terms.stream().map(UClass::getEndTime);
        List<Time> allTimes = Stream.concat(beginingTimes, endTimes).sorted().distinct().collect(Collectors.toCollection(ArrayList::new));
        return IntStream.range(0, allTimes.size() - 1).boxed().map(x -> new TermBox(allTimes.get(x), allTimes.get(x + 1))).collect(Collectors.toList());
    }

    private SortedSet<TermBox> getPossibleTargetBoxes(Swap swap, List<TermBox> termBoxes) {
        return termBoxes.stream()
                .filter( termBox -> termBox.getEnd().compareTo(swap.getTake().getEndTime()) >= 0)
                .collect(Collectors.toCollection(() -> new TreeSet<>(TermBox.defaultComparator())));
    }

    private Stream<Set<Swap>> getAllTargetDependingSwaps() {
        List<TermBox> possibleTermBoxes = possibleTermBoxes(marketData.getuClasses());
        Stream<Swap> swapStream = marketData.getStudents().stream().map(Student::getSwaps).flatMap(Collection::stream);
        Stream<ScatteredEdge> scatteredEdgeStream = swapStream.flatMap(swap -> getPossibleTargetBoxes(swap, possibleTermBoxes).stream().map(termBox -> new ScatteredEdge(swap, termBox)));
        return scatteredEdgeStream
                .collect(Collectors.groupingBy(ScatteredEdge::getEdgeTerm))
                .values()
                .stream()
                .map(scatteredEdges -> scatteredEdges.stream().map(ScatteredEdge::getBaseSwap).collect(Collectors.toSet()));
    }

    private Stream<Set<Swap>> getAllTargetDependingSwapsAdvanced() {
        List<TermBox> possibleTermBoxes = possibleTermBoxes(marketData.getuClasses());
        Stream<Swap> swapStream = marketData.getStudents().stream().map(Student::getSwaps).flatMap(Collection::stream);
        Stream<Set<Swap>> weekGroupedSwaps = swapStream.collect(Collectors.groupingBy(s -> s.getGive().getWeek(), Collectors.toSet())).values().stream();
        Stream<Set<Swap>> weekDayGroupedSwaps = weekGroupedSwaps
                .flatMap(swaps -> swaps.stream().collect(Collectors.groupingBy(s -> s.getGive().getWeekday(), Collectors.toSet())).values().stream());
        Stream<Set<ScatteredEdge>> scatteredEdgesStream = weekDayGroupedSwaps
                .map(swaps -> swaps.stream().flatMap(swap -> getPossibleTargetBoxes(swap, possibleTermBoxes).stream().map(termBox -> new ScatteredEdge(swap, termBox)))
                        .collect(Collectors.toSet()));
        return scatteredEdgesStream
                .flatMap(s -> s.stream().collect(Collectors.groupingBy(ScatteredEdge::getEdgeTerm, Collectors.mapping(ScatteredEdge::getBaseSwap, Collectors.toSet()))).values().stream());
    }

}
