package com.abin.mallchat.common.common.algorithm.ac;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Date;


@Slf4j
public class CreateTokenTest {

    @Test
    public void create(){
        String token = JWT.create()
                .withClaim("uid", 123L) // 只存一个uid信息，其他的自己去redis查
                .withClaim("createTime", new Date())
                .sign(Algorithm.HMAC256("dsfsdfsdfsdfsd")); // signature
        log.info("生成的token为 {}",token);


        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256("dsfsdfsdfsdfsd")).build();
            DecodedJWT jwt = verifier.verify(token);
            log.info(jwt.getClaims().toString());
        } catch (Exception e) {
            log.info("decode error,token:{}", token, e);
        }
    }

    @Test
    public void verifyToken(){
        String token = JWT.create()
                .withClaim("uid", 1) // 只存一个uid信息，其他的自己去redis查
                .withClaim("createTime", new Date())
                .sign(Algorithm.HMAC256("dsfsdfsdfsdfsd")); // signature
        log.info("生成的token为{}",token);
    }
}
