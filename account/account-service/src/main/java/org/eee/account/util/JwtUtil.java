package org.eee.account.util;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.eee.account.entity.UserPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class JwtUtil {

    // 密钥（需严格保密）
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("sk-84a8a0716afe4b379998dbab676be3e5sk-84a8a0716afe4b379998dbab676be3e5".getBytes(StandardCharsets.UTF_8));

    // 过期时间（示例：2小时）
    private static final long EXPIRE = TimeUnit.HOURS.toMillis(24);

    /**
     * 生成JWT Token
     * @param user 用户信息
     * @return JWT字符串
     */
    public static String generateToken(UserPrincipal user)
    {
        // Header信息
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");

        // Payload负载
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", user.getId());
        payload.put("username", user.getUsername());
        payload.put("authorities", user.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList()));
        payload.put("exp", new Date(System.currentTimeMillis() + EXPIRE)); // 过期时间
        payload.put("iat", new Date()); // 签发时间
        payload.put("iss", "eee"); // 签发者

        // 生成Token
        return Jwts.builder()
                .setHeaderParams(header)
                .setClaims(payload)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // 使用SecretKey和算法
                .compact();
    }

    public static boolean validateToken(String token)
    {
        try
        {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public static UserPrincipal parseToken(String token)
    {
        try
        {
            Map<String, Object> claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            UserPrincipal user = new UserPrincipal();
            user.setId(((Number) claims.get("userId")).longValue());
            user.setUsername((String) claims.get("username"));
            log.info("claim auth: {}", claims.get("authorities"));
            List<String> authorityList = (List<String>) claims.get("authorities");
            List<GrantedAuthority> authorities = authorityList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

            user.setAuthorities(authorities);

            return user;
       }
        catch (Exception e)
        {
            log.error("解析Token失败：{}", e.getMessage());
            return null;
        }
    }



}
