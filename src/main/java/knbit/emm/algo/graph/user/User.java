package knbit.emm.algo.graph.user;

import knbit.emm.model.Student;

import java.util.Objects;

public class User {
    private final Student student;

    public User(Student student) {
        this.student = student;
    }

    @Override
    public String toString() {
        return "User{" +
                "studentId=" + student.getStudentId() +
                '}';
    }

    public Student getStudent() {
        return student;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getStudent(), user.getStudent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStudent());
    }
}
