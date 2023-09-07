package com.example.stock.facade;

import com.example.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedissonLockStockFacade {

    private final RedissonClient redissonClient;

    private final StockService stockService;

    /**
     * 장점
     * : pub/sub 기반이므로 Redis의 부하를 줄여줌
     *
     * 단점
     * : 구현이 복잡함
     * : 별도의 라이브러리를 사용해야 함
     */
    public void decrease(final Long id, final Long quantity) {

        final RLock lock = redissonClient.getLock(id.toString());

        try {
            final boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);

            if (!available) {
                System.out.println("lock 획득 실패");
                return;
            }
            stockService.decrease(id, quantity);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
