package id.ac.ui.cs.gatherlove.campaigndonationwallet.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtValidationService {

    private final JwtProperties jwtProperties;
    private final JwtDecoder jwtDecoder;

    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            var key = jwtProperties.getKey();
            var verifier = new MACVerifier(key);

            if (!signedJWT.verify(verifier)) {
                log.warn("JWT signature verification failed");
                return false;
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            Date expirationTime = claimsSet.getExpirationTime();
            if (expirationTime == null || expirationTime.before(new Date())) {
                log.warn("JWT token expired");
                return false;
            }

            String issuer = claimsSet.getIssuer();
            if (jwtProperties.getIssuer() != null && 
                !jwtProperties.getIssuer().equals(issuer)) {
                log.warn("JWT issuer validation failed");
                return false;
            }

            return true;
        } catch (ParseException e) {
            log.error("Error parsing JWT: {}", e.getMessage());
            return false;
        } catch (JOSEException e) {
            log.error("Error verifying JWT signature: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }

    public Jwt parseJwt(String token) {
        try {
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            log.error("Error decoding JWT: {}", e.getMessage());
            throw new RuntimeException("Unable to parse JWT: " + e.getMessage(), e);
        }
    }
}