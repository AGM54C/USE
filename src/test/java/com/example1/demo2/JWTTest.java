package com.example1.demo2;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTTest {
    @Test
    public void testGen() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", "1");
        claims.put("nickname", "wyq");

        //生成JWT代码
        String token=JWT.create()
                .withClaim("user",claims)//添加载荷
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000*60*60*24))//添加过期时间
                .sign(Algorithm.HMAC256("Planet"));//指定配置密钥算法

        //打印token
        System.out.println(token);
    }

    @Test
    public void testParse() {
        //验证token
        String token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                ".eyJ1c2VyIjp7Im5pY2tuYW1lIjoid3lxIiwiaWQiOiIxIn0sImV4cCI6MTc0NzEzNDgxOH0" +
                ".Mpys8-u9oZWW3myelHX8oPteuev4cdqqozMWLFf0BjM";

        //密钥解密
        JWTVerifier jwtVerifier= JWT.require(Algorithm.HMAC256("Planet")).build();

        //生成一个解析后的jwt对象
        DecodedJWT jwt=jwtVerifier.verify(token);
        Map<String,Claim> claims=jwt.getClaims();
        System.out.println(claims.get("user"));

        //验证失败原因：
        //1.token不合法
        //2.token已过期
        //3.密钥不匹配
    }
}
