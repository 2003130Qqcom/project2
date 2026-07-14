package org.example.service;

/**
 * Token 黑名单服务。
 *
 * <p>基于 Redis 实现 JWT Token 的主动失效（登出）：
 * <ul>
 *   <li>登出时将 Token 加入黑名单，TTL 等于 Token 剩余有效期</li>
 *   <li>拦截器校验 Token 时额外检查是否已被拉黑</li>
 * </ul>
 * </p>
 */
public interface TokenService {

    /**
     * 将 Token 加入黑名单
     *
     * @param token      JWT 字符串
     * @param expireIn   剩余有效秒数
     */
    void blacklist(String token, long expireIn);

    /**
     * 检查 Token 是否已被拉黑
     *
     * @param token JWT 字符串
     * @return true 表示已失效
     */
    boolean isBlacklisted(String token);
}
