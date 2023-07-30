package com.abin.mallchat.custom.ac;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Date;


@Slf4j
public class CreateTokenTest {

    @Test
    public void create(){
        String token = JWT.create()
                .withClaim("uid", 10004L) // 只存一个uid信息，其他的自己去redis查
                .withClaim("createTime", new Date())
                .sign(Algorithm.HMAC256("dsfsdfsdfsdfsd")); // signature
        log.info("生成的token为 {}",token);


        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256("dsfsdfsdfsdfsc")).build();
            DecodedJWT jwt = verifier.verify(token);
            log.info(jwt.getClaims().toString());
        } catch (Exception e) {
            log.info("decode error,token:{}", token, e);
        }
    }
}
