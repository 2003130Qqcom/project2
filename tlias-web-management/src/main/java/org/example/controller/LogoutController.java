package org.example.controller;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.Result;
import org.example.service.TokenService;
import org.example.utils.JwtParseResult;
import org.example.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登出控制器。
 *
 * <p>将当前 Token 加入 Redis 黑名单，使其立即失效。
 * 后续请求携带该 Token 将被拦截器拒绝。</p>
 */
@Slf4j
@RestController
public class LogoutController {

    @Autowired
    private TokenService tokenService;

    @PostMapping("/logout")
    public Result logout(@RequestHeader("token") String token) {
        // 解析 Token 获取剩余有效期
        JwtParseResult parseResult = JwtUtils.parseTokenVerbose(token);
        if (parseResult.isSuccess()) {
            Claims claims = parseResult.getClaims();
            long now = System.currentTimeMillis();
            long expiration = claims.getExpiration().getTime();
            long remainSec = Math.max(1, (expiration - now) / 1000);
            tokenService.blacklist(token, remainSec);
            log.info("用户登出，Token 已拉黑，剩余有效期 {}s", remainSec);
        }
        return Result.success();
    }
}
