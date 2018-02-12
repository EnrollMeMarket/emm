package knbit.emm.algo.utils.pair;

public class Pair<FirstType, SecondType> {
    public final FirstType first;
    public final SecondType second;

    public Pair(FirstType first, SecondType second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "( " + first + ", " + second +")";
    }
}
