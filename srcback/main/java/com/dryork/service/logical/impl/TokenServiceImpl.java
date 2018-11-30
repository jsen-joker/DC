package com.dryork.service.logical.impl;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dryork.config.shiro.exception.ExpException;
import com.dryork.config.shiro.exception.OtherException;
import com.dryork.config.shiro.exception.TokenException;
import com.dryork.service.logical.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Service
public class TokenServiceImpl implements TokenService {
    /**
     *
     * @param data
     * @param userSec
     * @param exp seconds
     * @return
     * @throws UnsupportedEncodingException
     */
    @Override
    public String genToken(JSONObject data, String userSec, long exp) throws UnsupportedEncodingException {
        /*
        指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
         */
        Algorithm signatureAlgorithm = Algorithm.HMAC256(genKey(userSec));
        /*
        生成JWT的时间
         */
        long nowMills = System.currentTimeMillis();
        Date now = new Date(nowMills);

        JWTCreator.Builder builder = JWT.create()
                // 创建payload的私有声明（根据特定的业务需要添加，如果要拿这个做验证，一般是需要和jwt的接收方提前沟通好验证方式的）
                .withClaim("id", data.getIntValue("id"))
                .withClaim("openid", data.getString("openid"))
                .withClaim("telephone", data.getString("telephone"))
                //设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                .withJWTId(id)
                .withIssuedAt(now)
                .withIssuer("jsen")
                //sub(Subject)：代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userid，roldid之类的，作为什么用户的唯一标志。
                .withSubject(genSubject(data));

        if (exp >= 0) {
            long expMillis = nowMills + exp * 1000L;
            builder.withExpiresAt(new Date(expMillis));
        }
        return builder.sign(signatureAlgorithm);
    }

    @Override
    public DecodedJWT validToken(String token, String userSec, long exp) {
        try {
            //指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
            Algorithm signatureAlgorithm = Algorithm.HMAC256(genKey(userSec));
            if (exp >= 0) {
                return JWT.require(signatureAlgorithm).withIssuer("jsen").acceptExpiresAt(exp).build().verify(token);
            } else {
                return JWT.require(signatureAlgorithm).withIssuer("jsen").build().verify(token);
            }
        } catch (TokenExpiredException e) {
            throw new ExpException();
        } catch (JWTDecodeException e) {
            throw new TokenException();
        } catch (UnsupportedEncodingException e) {
            throw new OtherException();
        }
    }
    @Override
    public JSONObject genClaimsData(DecodedJWT decodedJWT) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", decodedJWT.getClaim("id").asString());
        jsonObject.put("openid", decodedJWT.getClaim("openid").asString());
        jsonObject.put("telephone", decodedJWT.getClaim("telephone").asString());
        return jsonObject;
    }

    @Override
    public int getUserId(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("id").asInt();
    }

    @Override
    public int getUserId(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim("id").asInt();
    }


    @Value("${spring.jwt.profile}")
    private String profile;
    @Value("${spring.jwt.id}")
    private String id;
    private static final String JWT_SECRET = "ai*))!@wlamrhnsdk$%*@u~ksdu34id^";
    private String genKey(String userSec) {
        return profile + JWT_SECRET + userSec;
    }
    private String genSubject(JSONObject data) {
        JSONObject object = new JSONObject();
        object.put("id", data.getString("id"));
        return object.toString();
    }
}
