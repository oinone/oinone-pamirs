package pro.shushi.pamirs.user.api.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.user.api.constants.UserConstant;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTokenUtil {

    private static final String CLAIM_KEY_NAME = "sub";

    /**
     * 30天(毫秒)
     */
    private static final long EXPIRATION_TIME = 2592000L;

    /**
     * JWT密码
     */
    private static final String SECRET = "mUsagy4zM4229d";

    /**
     * 签发JWT
     */
    public static String generateToken(String key) {
        return UserConstant.USER_TOKEN_PREFIX.concat(generateToken(key, EXPIRATION_TIME));
    }


    /**
     * 签发JWT
     *
     * @param key            加密key
     * @param expirationTime 过期时间
     * @return
     */
    public static String generateToken(String key, long expirationTime) {
        expirationTime = expirationTime * 1000;
        Map<String, Object> claims = new HashMap<>(1);
        claims.put(CLAIM_KEY_NAME, key);
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(Instant.now().toEpochMilli() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        return token;
    }

    /**
     * 验证JWT
     */
    public static Boolean validateToken(String token, String key) {
        String tokenKey = getKeyFromToken(token);
        return StringUtils.isNotBlank(key) && key.equals(tokenKey) && !isTokenExpired(token);
    }

    /**
     * 获取token是否过期
     */
    public static Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 根据token获取key
     */
    public static String getKeyFromToken(String token) {
        String key = getClaimsFromToken(token).getSubject();
        return key;
    }

    /**
     * 获取token的过期时间
     */
    public static Date getExpirationDateFromToken(String token) {
        Date expiration = getClaimsFromToken(token).getExpiration();
        return expiration;
    }

    /**
     * 解析JWT
     */
    private static Claims getClaimsFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }


}
