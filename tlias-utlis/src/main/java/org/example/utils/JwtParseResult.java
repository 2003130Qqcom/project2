package org.example.utils;

import io.jsonwebtoken.Claims;

/**
 * JWT Token 解析结果
 * <p>
 * 与 {@link JwtUtils#parseToken} 静默返回 null 不同，
 * 此类携带解析失败的详细原因，便于排查 Token 相关问题。
 * </p>
 *
 * <pre>
 * JwtParseResult result = JwtUtils.parseTokenVerbose(token);
 * if (!result.isSuccess()) {
 *     log.warn("Token解析失败 - type={}, reason={}", result.getFailureType(), result.getFailureReason());
 * }
 * </pre>
 */

public class JwtParseResult {

    private final boolean success;
    private final Claims claims;
    private final String failureType;
    private final String failureReason;

    private JwtParseResult(boolean success, Claims claims, String failureType, String failureReason) {
        this.success = success;
        this.claims = claims;
        this.failureType = failureType;
        this.failureReason = failureReason;
    }

    /**
     * 构建成功结果
     *
     * @param claims 解析后的 JWT Claims
     * @return 成功结果
     */
    public static JwtParseResult success(Claims claims) {
        return new JwtParseResult(true, claims, null, null);
    }

    /**
     * 构建失败结果
     *
     * @param type   失败类型：EXPIRED / BAD_SIGNATURE / MALFORMED / UNSUPPORTED / UNKNOWN
     * @param reason 人类可读的失败详情
     * @return 失败结果
     */
    public static JwtParseResult failure(String type, String reason) {
        return new JwtParseResult(false, null, type, reason);
    }

    public boolean isSuccess() {
        return success;
    }

    /**
     * @return 解析后的 Claims，仅在 {@link #isSuccess()} 为 true 时有值
     */
    public Claims getClaims() {
        return claims;
    }

    /**
     * @return 失败类型，如 EXPIRED、BAD_SIGNATURE、MALFORMED 等，成功时为 null
     */
    public String getFailureType() {
        return failureType;
    }

    /**
     * @return 失败原因的详细描述，成功时为 null
     */
    public String getFailureReason() {
        return failureReason;
    }

    @Override
    public String toString() {
        if (success) {
            return "JwtParseResult{success=true}";
        }
        return "JwtParseResult{success=false, failureType=" + failureType
                + ", reason=" + failureReason + "}";
    }
}
