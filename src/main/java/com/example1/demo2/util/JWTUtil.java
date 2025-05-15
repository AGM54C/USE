package com.example1.demo2.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.Map;

//jwt令牌解析工具
public class JWTUtil {
    //固定密钥形式为Planet
    private static final String KEY = "Planet";

    //生成Token
    public static String GenToken(Map<String, Object> claims) {
        //返回JWT代码
        return JWT.create()
                .withClaim("claims",claims)//添加载荷
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000*60*60*24))//过期时间24小时
                .sign(Algorithm.HMAC256(KEY));
    }

    //验证Token
    public static Map<String,Object> ParseToken(String token){
        //返回信息
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim("claims")
                .asMap();
    }

}
