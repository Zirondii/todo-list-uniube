package br.com.mvc.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/** Tokens assinados duram 30 minutos; a API não cria sessão HTTP. */
public class JwtService {
  private static final String EMISSOR = "tarefas-api";
  private final Algorithm algoritmo;
  private final JWTVerifier verificador;

  public JwtService(String segredo) {
    if (segredo == null || segredo.getBytes(StandardCharsets.UTF_8).length < 32) {
      throw new IllegalArgumentException("Defina JWT_SECRET com pelo menos 32 bytes.");
    }
    algoritmo = Algorithm.HMAC256(segredo);
    verificador =
        JWT.require(algoritmo)
            .withIssuer(EMISSOR)
            .withAudience("tarefas")
            .withClaimPresence("exp")
            .withClaimPresence("sub")
            .build();
  }

  public String emitir(long contaId) {
    Instant agora = Instant.now();
    return JWT.create()
        .withIssuer(EMISSOR)
        .withAudience("tarefas")
        .withSubject(Long.toString(contaId))
        .withIssuedAt(agora)
        .withExpiresAt(agora.plusSeconds(1800))
        .sign(algoritmo);
  }

  public DecodedJWT verificar(String token) {
    return verificador.verify(token);
  }
}
