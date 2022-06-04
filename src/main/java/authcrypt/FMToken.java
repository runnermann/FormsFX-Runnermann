package authcrypt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class FMToken {


    private static final String ISSUER = "flashmonkey2022";
    private static SecretKey SECKEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    /**
     * @param audience user orig_email
     * @param refreshLength number of 15 minute blocks
     * @return token
     */
    public static String createUserToken(String audience, int refreshLength) {
        long expires = System.currentTimeMillis() + (1000l * 60 * 10); // 10 minutes in the future
        return Jwts.builder()
                .setHeaderParam("kid", "thisIsNotEncryptedStuff")
                .setIssuer(ISSUER)
                .setAudience(audience)
                .setExpiration(new Date(expires))
                .setIssuedAt(new Date())
                //.claim("deck_id", deckJson.getString("deck_id"))
                //.claim("distrib_hash", deckJson.getString("distrib_hash"))
                // reset length * 15 minutes blocks
                //.claim("rl", refreshLength)
                .signWith(SECKEY)
                .compact();
    }
}
