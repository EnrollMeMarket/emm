package knbit.emm.utilities;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.crypto.MacProvider;
import knbit.emm.model.User;
import knbit.emm.model.UserRole;
import knbit.emm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
public class TokenUtils {

    private static UserRepository userRepository;
    private static final Key key = MacProvider.generateKey();

    @Autowired
    public TokenUtils(UserRepository userRepository) {
        TokenUtils.userRepository = userRepository;
    }

    public static String getStudentId(String token) throws SignatureException {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
    }

    private static Boolean verifyStudentId(String token, String studentId) throws ExpiredJwtException {
        try {
            return studentId.equals(getStudentId(token)) || verifyStarostaOrAdmin(token);
        } catch (SignatureException e) {
            return false;
        }
    }

    public static String buildAccessJWS(String studentId, long expiresIn) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiresIn))
                .setSubject(studentId)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    public static String buildRefreshJWS(String studentId) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setIssuedAt(new Date(now))
                .setSubject(studentId)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    public static UserRole getUserRole(String transcriptNumber) {
        User user = userRepository.findOne(transcriptNumber);
        if (user != null) return user.getUserRole();
        return UserRole.STUDENT;
    }


    public static Optional<ResponseEntity> prepareResponseEntityIfInvalidStudentToken(String token, String studentID) {
        try {
            if (!TokenUtils.verifyStudentId(token, studentID)) {
                return Optional.of(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
            }
        } catch (ExpiredJwtException e) {
            return Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN));

        }
        return Optional.empty();
    }

    public static Optional<ResponseEntity> prepareResponseEntityIfInvalidAdminOrStarostaToken(String token) {
        try {
            if (!TokenUtils.verifyStarostaOrAdmin(token)) {
                return Optional.of(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
            }
        } catch (ExpiredJwtException e) {
            return Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN));

        }
        return Optional.empty();
    }

    public static Boolean verifyStarostaOrAdmin(String token) throws ExpiredJwtException {
        try {
            User user = userRepository.findOne(getStudentId(token));
            return user!=null && (user.getUserRole() == UserRole.STAROSTA || user.getUserRole() == UserRole.ADMIN);
        } catch (SignatureException e) {
            return false;
        }
    }

}
