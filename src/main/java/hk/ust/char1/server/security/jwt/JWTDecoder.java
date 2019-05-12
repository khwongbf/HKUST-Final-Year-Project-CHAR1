package hk.ust.char1.server.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import static hk.ust.char1.server.security.SecurityConstants.*;

@Component
public class JWTDecoder {

	public DecodedJWT decodeFromRequest(WebRequest request){
		String token = request.getHeader(HEADER_STRING);
		if (token == null || token.isBlank() || token.isEmpty()){
			return null;
		}
		return JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
				.acceptExpiresAt(5)
				.acceptLeeway(1)
				.build()
				.verify(token.replace(TOKEN_PREFIX, ""));
	}

}
