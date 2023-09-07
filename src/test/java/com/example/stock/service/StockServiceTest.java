package com.example.stock.service;

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

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private PessimisticLockStockService pessimisticLockStockService;

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
    public void 재고감소() {

        // given
        stockService.decrease(1L, 1L);

        // when
        final Stock stock = stockRepository.findById(1L).orElseThrow();

        // then
        assertThat(stock.getQuantity()).isEqualTo(99L);
    }

    @Test
    @DisplayName("Transactional을 사용하지 않고 synchronized 키워드를 사용한 경쟁 조건 해결")
    public void 동시에_100개의_요청1() throws InterruptedException {

        // given
        final int threadCount = 100;

        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch latch = new CountDownLatch(threadCount); // 다른 스레드에서 실행중인 작업이 완료될 때까지 대기하도록 도와준다.

        // when
        for (int i = 0; i < threadCount; i++) {

            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L);
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

    @Test
    @DisplayName("Pessimistic Lock을 이용한 경쟁 조건 해결")
    public void 동시에_100개의_요청2() throws InterruptedException {

        // given
        final int threadCount = 100;

        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch latch = new CountDownLatch(threadCount); // 다른 스레드에서 실행중인 작업이 완료될 때까지 대기하도록 도와준다.

        // when
        for (int i = 0; i < threadCount; i++) {

            executorService.submit(() -> {
                try {
                    pessimisticLockStockService.decrease(1L, 1L);
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