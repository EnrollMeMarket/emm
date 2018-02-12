package knbit.emm.model;

import javax.persistence.*;

@Entity(name = "Tokens")
public class Token {

    @Id
    @GeneratedValue
    @Column
    private long tokenId;

    @OneToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    private Student student;

    @Column
    private String refreshToken;

    public Token(String refreshToken) {
        this.setRefreshToken(refreshToken);
    }

    public Token(){}
    public Student getStudent() {
        return student;
    }


    public void setStudent(Student student) {
        this.student = student;
    }

    public String getRefreshToken() { return refreshToken; }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return tokenId == token.tokenId;
    }
}
