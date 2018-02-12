package knbit.emm.model;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "Users")
public class User {

    public User() {
    }

    public User(String studentIndex, UserRole userRole) {
        this.studentIndex = studentIndex;
        this.userRole = userRole;
    }

    @Id
    @Column
    private String studentIndex;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public String getStudentIndex() {
        return studentIndex;
    }

    public void setStudentIndex(String studentIndex) {
        this.studentIndex = studentIndex;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "User{" +
                "studentIndex='" + studentIndex + '\'' +
                ", userRole=" + userRole +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(studentIndex, that.studentIndex) &&
                userRole == that.userRole;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentIndex, userRole);
    }
}
