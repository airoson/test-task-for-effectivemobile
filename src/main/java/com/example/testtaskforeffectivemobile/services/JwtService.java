package com.example.testtaskforeffectivemobile.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.testtaskforeffectivemobile.dtos.RefreshTokenValidationResult;
import com.example.testtaskforeffectivemobile.dtos.TokensPair;
import com.example.testtaskforeffectivemobile.entities.RefreshToken;
import com.example.testtaskforeffectivemobile.repositories.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
@Slf4j
public class JwtService {

    private String issuer;
    private JWTVerifier verifier;
    private Algorithm algorithm;
    private long accessTokenExpirationTimeMillis;
    private long refreshTokenExpirationTimeMillis;
    private RefreshTokenRepository tokenRepository;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.issuer}") String issuer,
                      @Value("${jwt.access-token-expiration-minutes}") int accessTokenExpirationTimeMinutes,
                      @Value("${jwt.refresh-token-expiration-days}") int refreshTokenExpirationTimeDays, RefreshTokenRepository tokenRepository){
        this.issuer = issuer;
        accessTokenExpirationTimeMillis = 60000L * accessTokenExpirationTimeMinutes;
        refreshTokenExpirationTimeMillis =  86400000L * refreshTokenExpirationTimeDays;
        algorithm = Algorithm.HMAC512(secret);
        verifier = JWT.require(algorithm).withIssuer(issuer).build();
        this.tokenRepository = tokenRepository;
    }

    public TokensPair generateNewTokensPair(String subject){
        String refreshToken = generateRefreshToken(subject);
        String accessToken = generateAccessToken(subject);
        return new TokensPair(refreshToken, accessToken);
    }

    public TokensPair regainAccessToken(String oldRefreshToken){
        RefreshTokenValidationResult res = verifyRefreshToken(oldRefreshToken);
        if(res != null){
            tokenRepository.deleteById(res.tokenId());
            String refreshToken = generateRefreshToken(res.subject());
            String accessToken = generateAccessToken(res.subject());
            return new TokensPair(refreshToken, accessToken);
        }
        return null;
    }


    /**
     * Сгенерировать access token
     * @param subject Пользователь, генерирующий токен
     * @return созданный access token, или null, если refresh token не валидный
     */
    public String generateAccessToken(String subject){
        return JWT.create().withIssuer(issuer)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusMillis(accessTokenExpirationTimeMillis))
                .withSubject(subject)
                .withClaim("authority", "USER")
                .sign(algorithm);
    }

    /**
     * Сгенерировать refresh token
     * @param subject Пользователь, владелец токена
     * @return сгенерированный refresh token
     */
    public String generateRefreshToken(String subject){
        RefreshToken token = new RefreshToken();
        Instant now = Instant.now();
        Instant expires = now.plusMillis(refreshTokenExpirationTimeMillis);
        token.setExpires(expires);
        tokenRepository.save(token);
        return JWT.create().withIssuer(issuer)
                .withIssuedAt(now)
                .withExpiresAt(expires)
                .withSubject(subject)
                .withClaim("tokenId", token.getId())
                .sign(algorithm);
    }

    /**
     * Проверить access token на валидность
     * Если токен валидный, возвращает subject, иначе null
     */
    public String verifyAccessToken(String accessToken){
        try{
            DecodedJWT verified = verifier.verify(accessToken);
            Claim authority = verified.getClaim("authority");
            if(!authority.isMissing() && !authority.isNull() && authority.asString().equals("USER")){
                return verified.getSubject();
            }
        }catch (JWTVerificationException e){
            log.info("Access token is not verified: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Проверить refresh token на валидность
     * Если токен валидный, возвращает subject, иначе null
     */
    public RefreshTokenValidationResult verifyRefreshToken(String refreshToken){
        try{
            DecodedJWT verified = verifier.verify(refreshToken);
            String tokenId = verified.getClaim("tokenId").asString();
            if(tokenRepository.existsById(tokenId)){
                return new RefreshTokenValidationResult(verified.getSubject(), tokenId);
            }
        }catch(JWTVerificationException e){
            log.info("Refresh token is not verified: {}", e.getMessage());
        }
        return null;
    }

}
