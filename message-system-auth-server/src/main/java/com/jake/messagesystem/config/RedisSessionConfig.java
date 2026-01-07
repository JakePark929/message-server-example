package com.jake.messagesystem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jake.messagesystem.constant.KeyPrefix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession(redisNamespace= KeyPrefix.USER_SESSION, maxInactiveIntervalInSeconds = 300)
public class RedisSessionConfig {
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));

        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
}
