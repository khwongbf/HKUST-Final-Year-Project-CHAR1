package hk.ust.char1.server.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collection;

import static hk.ust.char1.server.security.SecurityConstants.EXPIRATION_TIME;
import static hk.ust.char1.server.security.SecurityConstants.SECRET;

@Component
public class JWTTokenGenerator {
    public String generate(String username, Collection<? extends GrantedAuthority> grantedAuthorities){
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expiryTime = currentTime.plusSeconds(EXPIRATION_TIME);
        return Jwts.builder().setSubject(username + ":"+ grantedAuthorities.stream().map(GrantedAuthority::getAuthority).reduce("", (s, s2) -> s.equals("")? s2 : s.concat(",").concat(s2)) )
                .setIssuedAt(Date.from(currentTime.toInstant(ZoneId.systemDefault().getRules().getOffset(currentTime))))
                .setExpiration(Date.from(expiryTime.toInstant(ZoneId.systemDefault().getRules().getOffset(currentTime))))
                .signWith(SignatureAlgorithm.HS512, SECRET).compact();
    }
}
