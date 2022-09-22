package br.com.imd.model;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.util.Date;
public class JWTImpl {
    private Algorithm algorithm;

    public String generateJWTToken(String username, String password) {
        this.algorithm = Algorithm.HMAC256(password);
        String jwtToken = JWT.create()
                    .withIssuer("Simple Solution")
                    .withClaim("login", username)
                    .sign(algorithm);

            return jwtToken;
        }
}

