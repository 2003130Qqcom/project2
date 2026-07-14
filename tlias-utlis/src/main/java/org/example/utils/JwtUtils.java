package org.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 * 基于 jjwt 0.12.6 实现，提供 Token 生成与解析
 */
public class JwtUtils {

    /** 签名密钥（生产环境建议通过配置注入，长度至少 256 位） */
    private static final String SECRET = "tlias-web-management-secret-key-2026-spring-boot";
    /** Token 过期时间：24 小时 */
    private static final long EXPIRATION = 24 * 60 * 60 * 1000L;

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private JwtUtils() {
        // 工具类禁止实例化
    }

    /**
     * 生成 JWT Token
     *
     * @param claims 自定义声明（如 userId, username）
     * @return JWT 字符串
     */
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY)
                .compact();
    }

    /**
     * 解析 JWT Token（静默模式，向后兼容）
     * <p>
     * 解析失败时返回 null，不抛出异常。
     * 如需区分失败原因（过期/签名无效/格式错误），请使用 {@link #parseTokenVerbose(String)}。
     * </p>
     *
     * @param token JWT 字符串
     * @return 解析后的 Claims，解析失败返回 null
     */
    public static Claims parseToken(String token) {
        JwtParseResult result = parseTokenVerbose(token);
        return result.isSuccess() ? result.getClaims() : null;
    }

    /**
     * 解析 JWT Token（详细模式）
     * <p>
     * 与 {@link #parseToken(String)} 不同，此方法会区分具体的失败原因：
     * </p>
     * <ul>
     *   <li>{@code EXPIRED} — Token 已过期</li>
     *   <li>{@code BAD_SIGNATURE} — 签名不匹配或被篡改</li>
     *   <li>{@code MALFORMED} — Token 格式非法</li>
     *   <li>{@code UNSUPPORTED} — Token 类型不支持</li>
     *   <li>{@code UNKNOWN} — 其他未知错误</li>
     * </ul>
     *
     * @param token JWT 字符串
     * @return 解析结果，包含成功/失败状态、Claims 或失败原因
     */
    public static JwtParseResult parseTokenVerbose(String token) {
        if (token == null || token.isBlank()) {
            return JwtParseResult.failure("MALFORMED", "token is null or blank");
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return JwtParseResult.success(claims);
        } catch (ExpiredJwtException e) {
            return JwtParseResult.failure("EXPIRED",
                    "JWT expired at " + e.getClaims().getExpiration());
        } catch (UnsupportedJwtException e) {
            return JwtParseResult.failure("UNSUPPORTED",
                    "unsupported JWT: " + e.getMessage());
        } catch (MalformedJwtException e) {
            return JwtParseResult.failure("MALFORMED",
                    "malformed JWT: " + e.getMessage());
        } catch (SecurityException e) {
            return JwtParseResult.failure("BAD_SIGNATURE",
                    "invalid signature: " + e.getMessage());
        } catch (Exception e) {
            return JwtParseResult.failure("UNKNOWN",
                    e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
