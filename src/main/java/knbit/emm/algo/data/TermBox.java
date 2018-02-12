package knbit.emm.algo.data;

import java.sql.Time;
import java.util.Comparator;
import java.util.Objects;

public class TermBox {

    private final Time start;

    private final Time end;

    public TermBox(Time start, Time end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        else if (obj == null || !(obj instanceof TermBox)) return false;
        else {
            TermBox ref = (TermBox) obj;
            return Objects.equals(this.start, ref.start) && Objects.equals(this.start, ref.start);
        }
    }

    public static Comparator<? super TermBox> defaultComparator() {
        return Comparator.comparing(TermBox::getBeg).thenComparing(TermBox::getEnd);
    }

    public Time getBeg() {
        return start;
    }

    public Time getEnd() {
        return end;
    }
}
