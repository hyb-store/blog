package com.hyb;

import com.hyb.utils.MD5Utils;
import com.hyb.utils.RedisUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class BlogApplicationTests {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void contextLoads1() {
        redisUtils.set("key", "value");
    }

    @Test
    void contextLoads2() {
        System.out.println(redisTemplate.keys("*"));
        System.out.println(redisTemplate.opsForValue().get("key"));
    }

    @Test
    void contextLoads3() {
        System.out.println(MD5Utils.string2MD5("123456"));
        System.out.println(!"123456".equals(MD5Utils.string2MD5("e10adc3949ba59abbe56e057f20f883e")));
    }

}
