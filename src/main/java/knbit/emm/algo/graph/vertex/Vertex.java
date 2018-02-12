package knbit.emm.algo.graph.vertex;

import knbit.emm.model.UClass;

import java.util.Objects;

public class Vertex {
    private final UClass uClass;

    public Vertex(UClass uClass) {
        this.uClass = uClass;
    }

    public UClass getuClass() {
        return uClass;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "id='" + uClass.getClassId() + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return Objects.equals(uClass, vertex.uClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uClass);
    }
}
