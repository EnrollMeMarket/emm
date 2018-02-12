package knbit.emm.algo.graph.edge;

import knbit.emm.algo.graph.edge.state.State;
import knbit.emm.algo.utils.EqualityUtils;
import knbit.emm.model.Swap;

public class Edge {
    private final Swap swap;

    private State state = State.PENDING;

    public Edge(Swap swap) {
        this.swap = swap;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "owner=" + swap.getStudent().getStudentId() +
                '}';
    }

    public void accept() {
        state = State.ACCEPTED;
    }

    public void decline() {
        state = State.DECLINED;
    }

    public boolean isDeclined() {
        return state == State.DECLINED;
    }

    public State state() {
        return state;
    }

    public Swap getSwap() {
        return swap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        return swap != null ? EqualityUtils.jSwapsEqual(swap, edge.swap) : edge.swap == null && state == edge.state;

    }

    @Override
    public int hashCode() {
        int result = swap != null ? swap.hashCode() : 0;
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }
}
