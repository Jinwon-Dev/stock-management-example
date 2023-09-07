package com.example.stock.repository;

import com.example.stock.domain.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface StockRepository extends JpaRepository<Stock, Long> {

    /**
     * 장점
     * : 충돌이 빈번히 일어난다면, Optimistic Lock보다 성능이 좋음
     * : Lock을 통해 Update를 제어하기 때문에, 데이터 정합성이 보장됨
     * 
     * 단점
     * : 별도의 Lock을 잡기 때문에 성능 감소가 있을 수 있음
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s WHERE s.id = :id")
    Stock findByIdWithPessimisticLock(final Long id);
}
