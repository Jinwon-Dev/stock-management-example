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

    /**
     * 장점
     * : 별도의 Lock을 잡지 않으므로, Pessimistic Lock보다 성능상 이점이 있음
     *
     * 단점
     * : Update가 실패했을 경우, 재시도 로직을 직접 작성해줘야 함
     * : 충돌이 빈번하게 일어날 것으로 예상될 경우, 좋지 않다.
     */
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT s FROM Stock s WHERE s.id = :id")
    Stock findByIdWithOptimisticLock(final Long id);
}
