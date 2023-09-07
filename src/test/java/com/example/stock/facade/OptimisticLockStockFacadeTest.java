package com.example.stock.facade;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OptimisticLockStockFacadeTest {

    @Autowired
    private OptimisticLockStockFacade optimisticLockStockFacade;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void before() {
        stockRepository.saveAndFlush(new Stock(1L, 100L));
    }

    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }

    @Test
    @DisplayName("Optimistic Lock을 이용한 경쟁 조건 해결")
    public void 동시에_100개의_요청3() throws InterruptedException {

        // given
        final int threadCount = 100;

        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch latch = new CountDownLatch(threadCount); // 다른 스레드에서 실행중인 작업이 완료될 때까지 대기하도록 도와준다.

        // when
        for (int i = 0; i < threadCount; i++) {

            executorService.submit(() -> {
                try {
                    try {
                        optimisticLockStockFacade.decrease(1L, 1L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        final Stock stock = stockRepository.findById(1L).orElseThrow();

        // then
        assertThat(stock.getQuantity()).isEqualTo(0);
    }
}