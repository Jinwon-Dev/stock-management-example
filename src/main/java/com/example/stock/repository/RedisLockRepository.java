package com.example.stock.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisLockRepository {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 장점
     * : 구현이 간단함
     *
     * 단점
     * : 스핀락 방식이므로 Redis에 부하를 줄 수 있으므로, Thread.sleep을 통해 락 획득 재시도간에 텀을 줘야함
     */
    public Boolean lock(final Long key) {

        return redisTemplate
                .opsForValue()
                .setIfAbsent(generateKey(key), "lock", Duration.ofMillis(3_000));
    }

    public void unlock(final Long key) {
        redisTemplate.delete(generateKey(key));
    }

    private String generateKey(final Long key) {
        return key.toString();
    }
}
