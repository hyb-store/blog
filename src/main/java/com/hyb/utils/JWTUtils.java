package com.hyb.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.hyb.pojo.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT的构成：头部（header),载荷（payload)，签证（signature).
 *  header：
 *      声明类型，这里是jwt
 *      声明加密的算法 通常直接使用 HMAC SHA256
 *  playload：
 *      标准中注册的声明：
 *          iss: jwt签发者
 *          sub: jwt所面向的用户
 *          aud: 接收jwt的一方
 *          exp: jwt的过期时间，这个过期时间必须要大于签发时间
 *          nbf: 定义在什么时间之前，该jwt都是不可用的.
 *          iat: jwt的签发时间
 *          jti: jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。
 *      公共的声明：公共的声明可以添加任何的信息，一般添加用户的相关信息或其他业务需要的必要信息.该部分在客户端可解密.
 *      私有的声明：私有声明是提供者和消费者所共同定义的声明，一般不建议存放敏感信息，因为base64是对称解密的，意味着该部分信息可以归类为明文信息。
 *  signature：
 *      header (base64后的)
 *      payload (base64后的)
 *      secret
 * 这个部分需要base64加密后的header和payload使用.连接组成的字符串，然后通过header中声明的加密方式进行加盐secret组合加密，然后就构成了jwt的第三部分。
 */
public class JWTUtils {

    /**
     * 过期时间为1天
     */
    private static final long EXPIRE_TIME = 24*60*60*1000;

    /**secret是保存在服务器端的，jwt的签发生成也是在服务器端的，secret就是用来进行jwt的签发和jwt的验证，
     * 它就是你服务端的私钥，在任何场景都不应该流露出去。一旦客户端得知这个secret, 那就意味着客户端是可以自我签发jwt了
     * token私钥
     */
    private static final String TOKEN_SECRET = "joijsdfjlsjfljfljl5135313135";

    public static String createToken(User user) {
        //过期时间
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);  //前面加上currentTimeMillis来保证每次生成的token不一样

        //私钥及加密算法
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);

        //设置头信息
        Map<String, Object> header = new HashMap<>(2);
        header.put("typ", "JWT");
        header.put("alg", "HS256");

        //withHeader是封装Token的header
        // withClaim是传入payload
        // sign 是signature
        /*
          Algorithm 用来指定签名算法，常用的是HMAC256，加密因子可以是固定值，也可以是变量。
          withClaim指定签名里面存储的一些基本信息。
          withExpiresAt指定过期时间。
          withIssuedAt指定签发时间。
          withHeader指定token头，里面包含typ和alg，常用信息为JWT和HS256
          最终产生的token为base64url(header)+.+base64url(playload)，然后再用head里面指定的算法进行加密，获取对应token信息。
         */
        String token = JWT.create().withHeader(header)
                .withClaim("id", String.valueOf(user.getId())).withClaim("username", user.getUsername())
                .withClaim("avatar", user.getAvatar()).withClaim("email", user.getEmail())
                .withExpiresAt(date).sign(algorithm);

        return token;
    }

    /*
    解密之后获取到的是DecodedJWT类，通过getClaims()方法可以获取token里面存储的信息。
    获取该类需要调用JWTVerifier 的verify方法。
    获取JWTVerifier 类需要通过require().build()获取
     */
    public static DecodedJWT verifyToken(String token) throws Exception {
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
        JWTVerifier verifier = JWT.require(algorithm).build();
        //DecodedJWT 将jwt解密 将xxx.yyy.zzz 分开
        return verifier.verify(token);
    }

    public static String parserToken(String token, String claim) {
        return JWT.decode(token).getClaim(claim).asString();//可以获取token里面存储的信息
    }
}
