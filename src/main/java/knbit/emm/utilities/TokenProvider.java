package knbit.emm.utilities;

import knbit.emm.model.Token;
import knbit.emm.repository.StudentRepository;
import knbit.emm.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenProvider {

    private final TokenRepository tokenRepository;
    private final StudentRepository studentRepository;
    private final Map<String, String> tokenMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void loadTokensFromDatabase() {
        Iterable<Token> tokens = tokenRepository.findAll();
        tokens.forEach(e -> tokenMap.put(e.getRefreshToken(), e.getStudent().getStudentId()));
    }

    @Autowired
    public TokenProvider(TokenRepository tokenRepository, StudentRepository studentRepository){
        this.tokenRepository = tokenRepository;
        this.studentRepository = studentRepository;
    }

    @PreDestroy
    public void saveToDatabase(){
        Set<Token> tokens = new HashSet<>();
        for(Map.Entry<String, String> tokenEntry: tokenMap.entrySet()) {
            Token token = new Token(tokenEntry.getKey());
            token.setStudent(studentRepository.findByStudentId(tokenEntry.getValue()));
            tokens.add(token);
        }
        tokenRepository.save(tokens);
    }

    public Map<String, String> getTokenMap() {
        return tokenMap;
    }

    public String getStudentId(String token) {
        return tokenMap.get(token);
    }

    public void addToken(String token, String studentId) {
        removeByStudentId(studentId);
        tokenMap.put(token, studentId);
    }

    private void removeByStudentId(String studentId) {
        tokenMap.values().remove(studentId);
    }

    public void removeByToken(String token) {
        tokenMap.remove(token);
    }

    public Boolean exists(String token) {
        return tokenMap.containsKey(token);
    }
}
