package com.dryork.service.logical;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;

public interface TokenService {
    String genToken(JSONObject data, String userSec, long exp) throws UnsupportedEncodingException;

    DecodedJWT validToken(String token, String userSec, long exp);

    JSONObject genClaimsData(DecodedJWT decodedJWT);

    int getUserId(String token);
    int getUserId(DecodedJWT decodedJWT);
}
