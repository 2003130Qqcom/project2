package org.example;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class jwtTest {
    public static void main() {
        Map<String, Object> datamap =  new HashMap<>();
        datamap.put("username", "tom");
        datamap.put("password", "123456");
         String jwt =Jwts.builder().signWith(SignatureAlgorithm.HS256, "secret")
                .addClaims(datamap)
                .setExpiration(new Date( System.currentTimeMillis() + 1000 * 60 * 60 ))
                .compact();

    }

}
